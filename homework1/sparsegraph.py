from mpi4py import MPI
from hw1 import Hw1
import numpy as np

comm = MPI.COMM_WORLD
vectors = np.zeros((10,10), dtype = np.int)
# edges
vectors[0][1] = 1
vectors[1][2] = 1
vectors[1][5] = 1
vectors[2][7] = 1
vectors[3][4] = 1
vectors[3][6] = 1
vectors[3][9] = 1
vectors[4][7] = 1
vectors[5][6] = 1
vectors[5][7] = 1
vectors[7][8] = 1

vectors[1][0] = 1
vectors[2][1] = 1
vectors[4][3] = 1
vectors[5][1] = 1
vectors[6][3] = 1
vectors[6][5] = 1
vectors[7][2] = 1
vectors[7][4] = 1
vectors[7][5] = 1
vectors[8][7] = 1

myHomework = Hw1()
rank = comm.Get_rank()
myHomework.sparse_graph_coloring(vectors[rank], comm)