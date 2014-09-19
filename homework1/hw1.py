from mpi4py import MPI
import numpy as np
import math
import util
import random
import collections

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

		#### 1. Find min and max and bucket lenght

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


		#### 2. Each bucket finds in which buckets the assigned elemnts must go

		# Now we can scatter the array and each process might decide the
		# corresponding bucket for each number that has
		assigned_numbers = comm.scatter(data, root = 0)
		# This array will have a tuple in each position in the form (num, #bucket)
		assignation = []
		# The corresponding bucket number is (num-min)//bucket_length with int
		min_num = buckets_info[0]
		bucket_len = buckets_info[1]

		for num in assigned_numbers:
			assignation.append((num, ((num - min_num) // bucket_len)))

		data = comm.gather(assignation, root = 0)

		#### 3. First node sends the corresponding elements for each bucket
		
		if (rank == 0):
			data = np.concatenate(data)
			temp = [[] for i in range(n)]
			for number, bucket_id in data:
				if (bucket_id == n):
					bucket_id = n-1
				temp[bucket_id].append(number)
			for i in range(n - 1):
				comm.send(temp[i + 1], dest = (i + 1), tag = 1)
			data = temp[0]
		else:
			data = []
			data = comm.recv(source = 0, tag = 1)

		#### 4. Each bucket sorts its elements
		
		data.sort()

		#### 5. First node collects the sorted elements, knowing that the next
		####    bucket always has greater numbers
		
		data = comm.gather(data, root = 0)
		if (comm.rank == 0):
			return np.concatenate(data).tolist()

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

		#### 2. Distributively sort the sample using merge sort
		
		sorted_sample = self.bucket_sort(sample, comm)
		

		#### 3. Choose n -1 bucket separators
		
		if (rank == 0):
			for i in range(n_samples - n + 1):
				size = len(sorted_sample)
				del sorted_sample[random.randrange(size)]
			print ('separators ' + str(sorted_sample))
		sorted_sample = comm.bcast(sorted_sample, root = 0)

		#### 4. Distribute data in buckets
		
		if (rank == 0):
			data_partition = util.partition(data, n)
		else:
			data_partition = None
		data_part = comm.scatter(data_partition, root = 0)
		print ('data parts ' + str(data_part))
		
		###### 4.1 each bucket assigns the corresponding bucket of a group of
		######     elements
		
		assignation = []
		## TODO BORRAR ELEMENTOS DEL DATAPART
		for element in data_part:
			if (element > sorted_sample[-1]):
				assignation.append((element, n - 1))
			else:
				for i in range((len(sorted_sample))):
					separator = sorted_sample[i]
					if (element <= separator):
						assignation.append((element, i))
						break
		print ('assignation rank:' + str(rank) + ' = ' + str(assignation))
		
		###### 4.2 First node collects the assignations and sends elements to
		######	   their corresponding bucket
		
		assignations = comm.gather(assignation, root = 0)
		if (rank == 0):
			assignations = np.concatenate(assignations).tolist()
			temp = [[] for i in range(n)]
			for element, bucket_id in assignations:
				temp[bucket_id].append(element)
			for bucket in temp:
				for i in range(n - 1):
					comm.send(temp[i + 1], dest = (i + 1), tag = 2)
			data = temp[0]

		else:
			data = []
			data = comm.recv(source = 0, tag = 2)

		print ('bucket ' + str(rank) + ' -> ' + str(data))

		#### 5. Sort in each bucket and collect it

		data.sort()
		data = comm.gather(data, root = 0)
		if (rank == 0):
			print ('sorted data: ' + str(np.concatenate(data)))
			return np.concatenate(data).tolist()

	def sparse_graph_coloring(self, vector, comm):
		#vector es el vector de adyacencia del nodo asociado a comm.Get_rank()

		#### 1. Find the node/process with the max degree

		rank = comm.Get_rank()
		n = comm.Get_size()
		degree_id = (sum(vector), rank)
		degrees = comm.gather(degree_id, root = 0)
		if (rank == 0):
			max_degree = 0
			max_id = 0
			for degree_id in degrees:
				if degree_id[0] > max_degree:
					max_degree = degree_id[0]
					max_id = degree_id[1]
			print ('max degree: ' + str(max_degree))
			print ('max id: ' + str(max_id))

		#### 2. Start coloring from the node with max degree

			colors = [i for i in range(max_degree + 1)]
		else:
			colors = None
			max_id = None
		colors = comm.bcast(colors, root = 0)
		max_id = comm.bcast(max_id, root = 0)
		vectors = comm.gather(vector, root = max_id)
		# ColoredNode = collections.namedtuple('ColoredNode', ['id', 'col'])
		my_col = None

		def bfs_inlvl_coloring(node, lvl):
			'''
			In this function the nodes from a same level agree on which color
			each node would be, coordinating with the lowest, the previous and
			the next node.
			Starting from the lowest, a node choose the first color that hasn't
			been used and then tells to the next the colors that have been used.
			If a node doesn't have any color to choose it changes color with the
			lowest.
			'''
			# If this is the smallest node in the level
			# tag 1 is for prev -> next
			# tag 2 is for OK
			# tag 3 is for request color change
			# tag 4 is for reply color change
			counter = 0
			neighbors = []
			for i in range(len(vector)):
				if (vector[i] == 1):
					if (coloration[i] != None and coloration[i] in colors):
						s = 'Node {0}: color {1}, rank {2}'
						sf = s.format(rank, coloration[i], i)
						print (sf)
						colors.remove(coloration[i])
					if (i in level):
						neighbors.append(i)
			print ('colors n{0}: {1}'.format(rank, colors))
			print ('neighbors n{0}: {1}'.format(rank, neighbors))
			if (not neighbors):
				return colors[0]
			# If this is the first node in the level, 
			# this will be the coordinator
			if (rank < neighbors[0]):
				my_aux_col = colors[0]
				print ('n{0}->n{1}: {2}'.format(rank, neighbors[0], [my_aux_col]))
				comm.send([my_aux_col], dest = neighbors[0], tag = 1)
				# Receives changing color petitions
				for pos in range(2, len(neighbors)):
					node = neighbors[pos]
					answer = comm.recv(source = node, tag = 2)
					print ('n{0}<-n{1}: Answer {2}'.format(rank, node, answer))
					if (answer != 'OK'):
						print ('n{0}->n{1}: Reply {2}'.format(rank, node, my_aux_col))
						comm.send(my_aux_col, dest = node, tag = 4)
						my_aux_col = answer
						comm.recv(source = node, tag = 2)

			else:
				previous = 0
				if (rank > neighbors[len(neighbors) - 1]):
					previous = neighbors[len(neighbors) - 1]
					next = False
				else:
					for i in range(len(neighbors)):
						if (neighbors[i] > rank):
							previous = neighbors[i-1]
							next = neighbors[i]
							break
				first = neighbors[0]
				used_cols = comm.recv(source = previous, tag = 1)
				print ('Node {0}: used_cols {1}'.format(rank, used_cols))
				for col in used_cols:
					colors.remove(col)
				if not colors:
					comm.send(colored_father.col, dest = first, tag = 3)
					my_aux_col = comm.recv(source = first, tag = 4)
					comm.send('OK', dest = first, tag = 2)
				else:
					my_aux_col = colors[0]
					if (next):
						used_cols.append(my_aux_col)
						print ('n{0}->n{1}: {2}'.format(rank, next, used_cols))
						comm.send(used_cols + my_aux_col, dest = next, tag = 1)
						comm.send('OK', dest = first, tag = 2)

			return my_aux_col

		# Use bfs_coloring starting from the highest degree node and spreading
		# to the neighbors
		# tag 100 to say GO, tag 200 to informate assigned color
		if (rank == max_id):
			coloration = [None for i in range (n)]
			coloration[max_id] = 0
			my_col = 0
			breadth_levels = util.bfs_levels(rank, vectors)
			for level in breadth_levels:
				for node in level:
					comm.send((coloration, level), dest = node, tag = 100)
				for node in level:
					coloration[node] = comm.recv(source = node, tag = 200)
			print ('Final coloration ' + str(coloration))
			return coloration

		else:
			# waits until the coloration of all previous levels is sent to him
			coloration, level = comm.recv(source = max_id, tag = 100)
			print ('Node {0} started with {1}'.format(rank, coloration))
			my_col = bfs_inlvl_coloring(rank, level)
			comm.send(my_col, dest = max_id, tag = 200)
			print ('Node {0} decided its color is {1}'.format(rank, my_col))

	def shortest_paths(self, vector, comm):
		#vector es el vector de distancia a todos los demas nodos.La distancia
		#al mismo nodo es 0 y a los que no está conectado tambien
		return