package com.logbasex.sse;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {

	private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	//When a client disconnects or the connection times out, the SseEmitter object becomes unreachable from the client's perspective.
	// There are likely no other strong references holding onto it within the application logic
	private ConcurrentHashMap<WeakReference<SseEmitter>, Integer> sseEmitters = new ConcurrentHashMap<>();

	public void addEmitter(final SseEmitter emitter) {
		emitters.add(emitter);
		emitter.onCompletion(() -> emitters.remove(emitter));
		emitter.onTimeout(() -> emitters.remove(emitter));
	}

	@Scheduled(fixedRate = 1000)
	public void sendEvents() {
		for (final var emitter : emitters) {
			try {
				emitter.send(Instant.now());
			} catch (IOException e) {
				emitter.complete();
				emitters.remove(emitter);
			}
		}
	}
}
