package com.logbasex.grpcsquareservice;

import com.logbasex.proto.Input;
import com.logbasex.proto.Output;
import com.logbasex.proto.SquareRpcGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class GrpcSquareService extends SquareRpcGrpc.SquareRpcImplBase {
	
	@Override
	public void findSquareUnary(Input request, StreamObserver<Output> responseObserver) {
		int number = request.getNumber();
		responseObserver.onNext(
				Output.newBuilder().setNumber(number).setResult(number * number).build()
		);
		responseObserver.onCompleted();
	}
	
	@Override
	public StreamObserver<Input> findSquareBiStream(StreamObserver<Output> responseObserver) {
		return new StreamObserver<Input>() {
			@Override
			public void onNext(Input input) {
				Integer number = input.getNumber();
				Output output = Output.newBuilder()
						.setNumber(number)
						.setResult(number * number).build();
				responseObserver.onNext(output);
			}
			
			@Override
			public void onError(Throwable throwable) {
				responseObserver.onCompleted();
			}
			
			@Override
			public void onCompleted() {
				responseObserver.onCompleted();
			}
		};
	}
	
}
