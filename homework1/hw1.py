from mpi4py import MPI
import numpy as np
import math
import util
import random

class Hw1:

	def __init__(self):
		return

	#comm es el objeto communicador que permite envíar mensajes a los demás procesos
	
	def bucket_sort(self, data, comm):
		#data es el subarreglo asignado al proceso
		#comm permite comunicarse con los demas procesos
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
			data = util.partition(data, n)
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
		data = comm.gather(data, root=0)
		if (comm.rank == 0):
			return data

	def sample_sort(self,data,n_samples,comm):
		#n_samples es el número de muestras que se tomaran
		#n_samples>comm.Get_size()
		#m lo dejaremos como comm.Get_size()-1
		rank = comm.Get_rank()
		# n is the number of processes
		n = comm.Get_size()

		#### 1. Choose a random sample from the data array
		if (rank == 0):
			indices = [i for i in range(len(data))]
			random_indices = []
			for i in range(n_samples):
				r = random.randrange(len(indices))
				random_indices.append(indices.pop(r))
			sample = []
			for index in random_indices:
				sample.append(data[index])
		else:
			sample = None
		sample = comm.bcast(sample, root = 0)

		#### 2. Parallely sort the sample using merge sort
		sorted_sample = self.bucket_sort(sample, comm)
		if (rank == 0):
			sorted_sample = np.concatenate(sorted_sample).tolist()
			print (sorted_sample)
			for i in range(n_samples - n + 1):
				size = len(sorted_sample)
				del sorted_sample[random.randrange(size)]
			print (sorted_sample)
		#### 3. Choose n -1 bucket separators
		# for i in range(n_samples - n + 1):
		return

	def sparse_graph_coloring(self, vector, comm):
		#vector es el vector de adyacencia del nodo asociado a comm.Get_rank()
		return

	def shortest_paths(self, vector, comm):
		#vector es el vector de distancia a todos los demas nodos.La distancia al
		#mismo nodo es 0 y a los que no está conectado tambien
		return