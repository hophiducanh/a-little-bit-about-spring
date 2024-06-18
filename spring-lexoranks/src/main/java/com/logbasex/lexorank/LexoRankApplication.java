package com.logbasex.lexorank;


import com.github.pravin.raha.lexorank4j.LexoRank;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LexoRankApplication {

	public static void main(String[] args) {
        LexoRank minLexoRank = LexoRank.min();
        LexoRank maxLexoRank = LexoRank.max();
        LexoRank middleLexoRank = LexoRank.middle();
        LexoRank parsedLexoRank = LexoRank.parse("0|0i0000:");
        System.out.println(minLexoRank.format());
        System.out.println(middleLexoRank.format());
        System.out.println(maxLexoRank.format());
		SpringApplication.run(LexoRankApplication.class, args);
	}
}
