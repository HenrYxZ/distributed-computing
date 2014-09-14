from mpi4py import MPI
from hw1 import Hw1
'''
This module sorts an array using multiple processes and the samplesort
algorithm.

@author Hernaldo Henriquez September 2014
'''

comm = MPI.COMM_WORLD
data = [48, 50, 18, 61, 53, 26, 57, 48, 29, 20, 46, 38, 60, 32, 43, 35, 64, 21,
 50, 25, 52, 62, 58, 57]

myHomework = Hw1()
myHomework.sample_sort(data, 8, comm)