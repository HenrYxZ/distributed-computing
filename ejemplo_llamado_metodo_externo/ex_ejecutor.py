from mpi4py import MPI
from ex_entregable import your_class
import numpy

if __name__=="__main__":
	comm=MPI.COMM_WORLD

	size=comm.Get_size()
	rank=comm.Get_rank()

	if rank==0:
		data=[(i+1)**2 for i in range(size)]

	else:
		data=None

	data = comm.scatter(data,root=0)
	yourobject=your_class()
	yourobject.your_method1(data,comm)