# Introduction
This project, based on Java 8 and Spring boot, is to learn and demostrate the difference between sequential and parallel service call, and how the system performance is influenced by them.

# Api structure

<img src="./src/docs/images/overall.png"/>

1. A slow api, which sleeps for 2 seconds, is located in /legacy/slow/{id}. {id} can be any Integer number in order to represent to different apis.
2. Other three apis /seq/combined, /parallel/combined, /webflux/combined are represented for a combined api, which combine the backend apis in a different way.
 2.1 For simplify the example, they all call the slow api twice with different paramerters 1 and 2;
 2.2 /seq/combined calls the slow apis by sequence way;
 2.3 /parallel/combined calls the slow apis by parallel way(Asychronous by using akhttp);
 2.4 /webflux/combined calls the slow apis by reactive way(Stream by using spring-webflux);

<img src="src/docs/uml/seq.png"/>
Sequence diagram of Sequence combined api

<img src="src/docs/uml/parallel-strea.png"/>
Sequence diagram of Parallel and Stream api