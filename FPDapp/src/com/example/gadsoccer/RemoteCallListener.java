package com.example.gadsoccer;

public interface RemoteCallListener<E> {
	void onRemoteCallListenerComplete(E dati );
	void onExecuteRemoteCall();
}
