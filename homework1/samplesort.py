from mpi4py import MPI
import numpy as np
import random
'''
This module sorts an array using multiple processes and the samplesort
algorithm.

@author Hernaldo Henriquez September 2014
'''

def sample_sort(data, n_samples, comm):
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

		print (sample)



comm = MPI.COMM_WORLD
data = [48, 50, 18, 61, 53, 26, 57, 48, 29, 20, 46, 38, 60, 32, 43, 35, 64, 21,
 50, 25, 52, 62, 58, 57]
sample_sort(data, 5, comm)