from mpi4py import MPI
from hw1 import Hw1
import numpy as np

comm = MPI.COMM_WORLD
vectors = np.zeros((8,8), dtype = np.int)
# edges
vectors[0][1] = 1
vectors[1][2] = 2
vectors[1][3] = 2
vectors[2][4] = 3
vectors[2][5] = 1
vectors[3][5] = 1
vectors[4][5] = 1
vectors[4][6] = 3
vectors[5][7] = 1

for a in range(8):
	for b in range(a+1, 8):
		if (vectors[a][b] != 0):
			vectors[b][a] = vectors[a][b]

myHomework = Hw1()
rank = comm.Get_rank()
source_node = 1
myHomework.shortest_paths(vectors[rank], source_node, comm)