package com.example.demo;


import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DojoReactiveTest {

    @Test
    void converterData() {
        List<Player> list = CsvUtilFile.getPlayers();
        assert list.size() == 18207;
    }

    @Test
    void jugadoresMayoresA35() {
        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(list);

        observable.filter(jugador -> jugador.getAge() > 35)
                .subscribe(System.out::println);
    }


    @Test
    void jugadoresMayoresA35SegunClub() {
        List<Player> readCsv = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(readCsv);

        observable.filter(player -> player.getAge() > 35)
                .distinct()
                .groupBy(Player::getClub)
                .flatMap(groupedFlux -> groupedFlux
                        .collectList()
                        .map(list -> {
                            Map<String, List<Player>> map = new HashMap<>();
                            map.put(groupedFlux.key(), list);
                            return map;
                        }))
                .subscribe(map -> {
                    map.forEach((key, value) -> {
                        System.out.println("\n");
                        System.out.println(key + ": ");
                        value.forEach(System.out::println);
                    });
                });

    }


    @Test
    void mejorJugadorConNacionalidadFrancia() {
        Mono<Player> bestFrance = Flux.fromIterable(CsvUtilFile.getPlayers())
                .filter(ply -> ply.getNational().equals("France"))
                .reduce((ant, act) -> ant.getWinners() > act.getWinners() ? ant : act);

        bestFrance.subscribe(System.out::println);

    }

    @Test
    void clubsAgrupadosPorNacionalidad() {
        Flux<Map<String, String>> clubs = Flux.fromIterable(CsvUtilFile.getPlayers())
                .sort(Comparator.comparing(Player::getNational))
                .distinct()
                .flatMap(player -> {
                    if (player.getClub().isEmpty() | player.getNational().isEmpty()) {
                        return Mono.empty();
                    }
                    Map<String, String> club = new LinkedHashMap<>();
                    club.put(player.getNational(), player.getClub());
                    return Mono.just(club);
                });

        clubs.subscribe(System.out::println);

    }

    @Test
    void clubConElMejorJugador() {
        Mono<String> clubBestPlayer = Flux.fromIterable(CsvUtilFile.getPlayers())
                .reduce((ant, act) -> ant.getWinners() > act.getWinners() ? ant : act)
                .map(Player::getClub);

        clubBestPlayer.subscribe(System.out::println);
    }

    @Test
    void clubConElMejorJugador2() {
    }

    @Test
    void ElMejorJugador() {
        Mono<Player> bestPlayer = Flux.fromIterable(CsvUtilFile.getPlayers())
                .reduce((ant, act) -> ant.getWinners() > act.getWinners() ? ant : act);

        bestPlayer.subscribe(System.out::println);

    }

    @Test
    void ElMejorJugador2() {


    }

    @Test
    void mejorJugadorSegunNacionalidad() {
        Set<String> nationalitieSet = CsvUtilFile.getPlayers().stream()
                .sorted(Comparator.comparing(Player::getNational))
                .flatMap(player -> Stream.of(player.getNational()))
                .collect(Collectors.toSet());

        Mono<Set<String>> nationalities = Mono.just(nationalitieSet);

        Mono<List<Player>> players = Mono.just(CsvUtilFile.getPlayers());

        Mono<List<NationalityPlayerDTO>> bestPlayers = players.zipWith(nationalities,
                (play, nat) -> {
                    return nat.stream().map(nationality -> {
                                Player winner = play.stream()
                                        .filter(player -> player.getNational().equals(nationality))
                                        .reduce((acum, player) -> {
                                            return acum.getWinners() > player.getWinners() ? acum : player;
                                        })
                                        .get();
                                return new NationalityPlayerDTO(nationality, winner);
                            })
                            .collect(Collectors.toList());
                });

        bestPlayers.subscribe(element -> {
            element.forEach(nat -> System.out.println(nat.getNationality() + "-" + nat.getPlayer().getName()));
        });
    }
}
