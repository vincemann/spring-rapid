package io.github.vincemann.demo.service.springDataJPA.it;

/*
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test", "springdatajpa"})
public class VetJPAServiceTest extends CrudServiceTest<VetJPAService, Vet, Long> {

    public VetJPAServiceTest(@Autowired VetJPAService crudService) {
        super(crudService, transactionManager, repository);
    }


    @Override
    protected List<SuccessfulSaveServiceTestBundle<Vet>> provideSuccessfulSaveTestEntityBundles() {
        return Lists.newArrayList(
                new SuccessfulSaveServiceTestBundle<>(
                        Vet.builder()
                        .firstName("meister")
                        .lastName("yoda")
                        .build())
        );
    }

}
*/