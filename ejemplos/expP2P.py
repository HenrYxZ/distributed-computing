from mpi4py import MPI

#Envio de un objeto python mediante comunicacion Punto Punto

comm=MPI.COMM_WORLD
rank = comm.Get_rank()

if rank==0:
	data = {'a': 7,'b': 3.14}
	print ("0: "+str(data))
	comm.send(data,dest=1,tag=11)
elif rank==1:
	data={}
	print ("1bef: "+str(data))
	data=comm.recv(source=0,tag=11)
	print ("1aft: "+str(data))