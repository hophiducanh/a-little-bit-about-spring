package com.logbasex.aggregatorservice.service;

import com.logbasex.proto.Input;
import com.logbasex.proto.Output;
import com.logbasex.proto.SquareRpcGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class GrpcAPIService extends SquareRpcGrpc.SquareRpcImplBase {
	@GrpcClient("grpc-square-service")
	private SquareRpcGrpc.SquareRpcBlockingStub squareClientStub;
	
	//TODO
	//https://viblo.asia/p/grpc-grpc-vs-rest-performance-oOVlYv1458W
//	@Override
//	public StreamObserver<Input> findSquareBiStream(StreamObserver<Output> responseObserver) {
//		StreamObserver<Input> biStream = super.findSquareBiStream(responseObserver);
//		return biStream;
//	}
//
//
//	public String getSquareResponseStream(int number) {
//		StreamObserver<Output> streamObserver = new StreamObserver<Output>() {
//			@Override
//			public void onNext(Output value) {
//
//			}
//
//			@Override
//			public void onError(Throwable t) {
//
//			}
//
//			@Override
//			public void onCompleted() {
//
//			}
//		};
//
//		StreamObserver<Input> biStream = super.findSquareBiStream(streamObserver);
//		biStream.onCompleted();
//	}
	
	public Object getSquareResponseUnary(int number) {
		Input input = Input.newBuilder()
				.setNumber(number)
				.build();
		
		Output output = this.squareClientStub.findSquareUnary(input);
		return output.getNumber();
	}
}
