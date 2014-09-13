from mpi4py import MPI

#broadcasting de un diccionario Python

comm=MPI.COMM_WORLD
rank=comm.Get_rank()

if rank==0:
	data={'key1': [7,2.72,2+3j],
		  'key2': ('abc','xyz')}
else:
	data=None
data=comm.bcast(data,root=0)
print (str(rank)+":"+str(data))