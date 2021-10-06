package com.disertatie.Middleware.Rest;

public class UniversalController {
//    @Autowired
//    private ReactiveCountryRepository reactiveCountryRepository;
//    @Autowired
//    private ReactiveCityRepository reactiveCityRepository;
//
//    static private Integer counter = 0;
//
//    @GetMapping("/test1")
//    public Mono<ResponseEntity<Integer>> test1() {
//        System.out.println("[test1] Started: " + counter);
//        int c = counter;
//        counter += 1;
//
//        var mono = Mono.defer(() -> Mono.just(new ResponseEntity<>(c, OK)))
//                .delayElement(Duration.ofMillis(500))
//                .map(element -> {
//                    System.out.println("[test1] Finished: " + c);
//                    return element;
//                });
//
//        return FluidIO.fluidHandle(mono, "/test1");
//    }
//
//    @GetMapping("/test2")
//    public Mono<ResponseEntity<BigInteger>> test2(@RequestParam Integer n) {
//        System.out.println("[test2] Started: " + counter);
//        int c = counter;
//        counter += 1;
//
//        var mono = Mono.defer(() -> Mono.just(new ResponseEntity<>(Algorithms.fibonacci(n), OK)))
//                .map(element -> {
//                    System.out.println("[test2] Finished: " + c);
//                    return element;
//                });
//
//        return FluidIO.fluidHandle(mono, "/test2");
//    }
//
//    @GetMapping("/test3")
//    public Mono<ResponseEntity<BigInteger>> test3(@RequestParam Integer m, @RequestParam Integer n) {
//        System.out.println("[test3] Started: " + counter);
//        int c = counter;
//        counter += 1;
//
//        var mono = Mono.defer(() -> Mono.just(new ResponseEntity<>(Algorithms.ack(m, n), OK)))
//                .map(element -> {
//                    System.out.println("[test3] Finished: " + c);
//                    return element;
//                });
//
//        return FluidIO.fluidHandle(mono, "/test3");
//    }
//
//    @GetMapping("/test4")
//    public Mono<ResponseEntity<BigInteger>> test4(@RequestParam Integer m, @RequestParam Integer n, @RequestParam String ip) {
//        System.out.println("[test4] Started: " + counter);
//        int c = counter;
//        counter += 1;
//
//        var mono = Mono.defer(() -> Mono.just(Algorithms.ack(m, n)))
//                .flatMap(result -> callSecondServer(result, ip))
//                .map(response -> new ResponseEntity<>(response, OK))
//                .map(element -> {
//                    System.out.println("[test4] Finished: " + c);
//                    return element;
//                });
//
//        return FluidIO.fluidHandle(mono, "/test4");
//    }
//
//    @GetMapping("/test5")
//    public Mono<ResponseEntity<List<String>>> test5(@RequestParam(name = "country") String countryName) {
//        System.out.println("[test5] Started: " + counter);
//        int c = counter;
//        counter += 1;
//        Supplier<ResponseEntity<List<String>>> blockingSupplier = () -> {
//            try{
//                Class.forName("org.postgresql.Driver");
//                Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/dvdrental","postgres","root");
//                Statement stmt = con.createStatement();
//                ResultSet rs = stmt.executeQuery("SELECT country_id FROM country where country = '" + countryName + "';");
//                rs.next();
//                var countryId = rs.getInt(1);
//                rs = stmt.executeQuery("SELECT city FROM city where country_id = " + countryId +";");
//                List<String> cities = new ArrayList<>();
//                while(rs.next())
//                    cities.add(rs.getString(1));
//                con.close();
//                System.out.println("[test5] Finished: " + c);
//                return new ResponseEntity<>(cities, OK);
//            } catch(Exception e) {
//            }
//
//            List<String> cities = new ArrayList<>();
//            System.out.println("[test45] Failed: " + c);
//            return new ResponseEntity<>(cities, OK);
//        };
//
//        Supplier<Mono<ResponseEntity<List<String>>>> nonblockingSupplier = () ->
//                Mono.defer(() -> reactiveCountryRepository.findOneByName(countryName))
//                        .map(country -> reactiveCityRepository.findAllByCountryId(country.id))
//                        .flatMap(Flux::collectList)
//                        .map(cities -> cities.stream().map(city -> city.name).collect(Collectors.toList()))
//                        .map(cities -> new ResponseEntity<>(cities, OK))
//                        .map(element -> {
//                            System.out.println("[test5] Finished: " + c);
//                            return element;
//                        });
//
//        return FluidIO.fluidSwitch(blockingSupplier, nonblockingSupplier, "/test5");
//    }
//
//    private Mono<BigInteger> callSecondServer(BigInteger ackermannResponse, String ip) {
//        WebClient webClient = WebClient.create("http://" + ip + ":8081");
//        String uri = "/test3/?m=" + ackermannResponse.mod(BigInteger.valueOf(3)) +
//                "&n=" + ackermannResponse.mod(BigInteger.valueOf(10));
//        return webClient.get().uri(uri).retrieve().bodyToMono(BigInteger.class);
//    }
}
