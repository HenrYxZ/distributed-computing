from mpi4py import MPI
import numpy as np
'''
This module sorts an array using multiple processes and the bucketsort
algorithm.

@author Hernaldo Henriquez September 2014
'''

def bucket_sort(data, comm):
	rank = comm.Get_rank()
	# n is the number of processes
	n = comm.Get_size()
	if (rank == 0):
		# find the min (a) and the max (b) number in the array
		max_num = data[0]
		min_num = data[0]
		for i in range(len(data)):
			if (data[i] < min_num):
				min_num = data[i]
			elif (data[i] > max_num):
				max_num = data[i]
		# broadcast the bucket division found
		bucket_len = (max_num - min_num) // n
		# the lower limit for each bucket i is a_i = a + i * bucket_length
		buckets_info = (min_num, bucket_len)
		data = np.array(data)
		data = np.split(data, n)
	else:
		buckets_info = None
	buckets_info = comm.bcast(buckets_info, root = 0)

	# Now we can scatter the array and each process might decide the
	# corresponding bucket for each number that has
	assigned_numbers = comm.scatter(data, root = 0)
	# This array will have a tuple in each position in the form (num, #bucket)
	assignation = []
	# The corresponding bucket number is (num-min)//bucket_length with int
	min_num = buckets_info[0]
	bucket_len = buckets_info[1]

	for num in assigned_numbers:
		assignation.append((num, ((num-min_num)//bucket_len)))
	assignation = np.array(assignation)

	data = comm.gather(assignation, root=0)

	if (rank == 0):
		data = np.concatenate(data)
		temp = [[] for i in range(n)] 
		for number, bucket_id in data:
			# print (str(number) + " , " + str(bucket_id))
			if (bucket_id == n):
				bucket_id = n-1
			temp[bucket_id].append(number)
		for i in range(n-1):
			comm.send(temp[i+1], dest=(i+1), tag=1)
		data = temp[0]
	else:
		data = []
		data = comm.recv(source=0, tag=1)

	data.sort()
	print (data)
	data = comm.gather(data, root=0)
	if (rank == 0):
		print ('final' + str(np.concatenate(data)))
comm = MPI.COMM_WORLD
data = [48, 50, 18, 61, 53, 26, 57, 48, 29, 20, 46, 38, 60, 32, 43, 35, 64, 21,
 50, 25, 52, 62, 58, 57]
bucket_sort(data, comm)