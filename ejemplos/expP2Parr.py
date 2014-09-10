from mpi4py import MPI
import numpy as np
import time

comm=MPI.COMM_WORLD
rank=comm.Get_rank()

t0 = time.time()
#envío explicito del tipo de datos
if rank==0:
	data=np.arange(1000,dtype='i')
	comm.Send([data,MPI.INT],dest=1,tag=77)
elif rank==1:
	data=np.empty(1000,dtype='i')
	comm.Recv([data,MPI.INT],source=0,tag=77)
print (str(rank) + 'proceso : ' + str(time.time() - t0))

#envío implicito
t0 = time.time()
if rank==0:
	data=np.arange(1000,dtype=np.float64)
	comm.Send(data,dest=1,tag=13)
elif rank==1:
	data=np.empty(1000,dtype=np.float64)
	comm.Recv(data,source=0,tag=13)	
print (str(rank) + 'proceso : ' + str(time.time() - t0))