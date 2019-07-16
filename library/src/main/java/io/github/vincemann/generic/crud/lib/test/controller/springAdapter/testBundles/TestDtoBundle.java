package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.util.BeanUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TestDtoBundle<Dto extends IdentifiableEntity> {

    private Dto dto;
    //for update test
    private List<UpdateTestBundle<Dto>> updateTestBundles = new ArrayList<>();
    @Setter
    private PostCreateCallback<Dto> postCreateCallback = (dto) -> {};
    @Setter
    private PostDeleteCallback<Dto> postDeleteCallback = (dto) -> {};
    @Setter
    private PostFindCallback<Dto> postFindCallback = (dto) -> {};

    /**
     *
     * @param dto
     * @param updateTestBundles  for every given {@link UpdateTestBundle}, there will be a update test,
     *                           trying to update the {@link TestDtoBundle#dto} with the {@link UpdateTestBundle#getModifiedDto()}.
     *
     */
    @Builder
    public TestDtoBundle(Dto dto, List<UpdateTestBundle<Dto>> updateTestBundles, PostCreateCallback<Dto> postCreateCallback, PostDeleteCallback<Dto> postDeleteCallback, PostFindCallback<Dto> postFindCallback) {
        this.dto = dto;
        this.updateTestBundles = updateTestBundles;
        if(this.updateTestBundles==null){
            this.updateTestBundles = new ArrayList<>();
        }
        this.postCreateCallback = postCreateCallback;
        if(this.postCreateCallback == null){
            this.postCreateCallback= (dto1) -> {};
        }
        this.postDeleteCallback = postDeleteCallback;
        if(this.postDeleteCallback ==null){
            this.postDeleteCallback = (dto1) -> {};
        }
        this.postFindCallback = postFindCallback;
        if(this.postFindCallback == null){
            this.postFindCallback = (dto1) -> {};
        }
        verifyBundle();
    }

    public TestDtoBundle(Dto dto) {
        this.dto = dto;
        verifyBundle();
    }

    public TestDtoBundle(Dto dto, UpdateTestBundle<Dto> updateTestBundle) {
        this.dto = dto;
        this.updateTestBundles.add(updateTestBundle);
        verifyBundle();
    }

    public TestDtoBundle(Dto dto, Dto... modifiedDto){
        this.dto=dto;
        for (Dto modDto : modifiedDto) {
            this.updateTestBundles.add(new UpdateTestBundle<>(modDto));
        }
        verifyBundle();
    }

    protected void verifyBundle(){
        Assertions.assertNotNull(dto);
        updateTestBundles.forEach(bundle -> {
            Assertions.assertNotNull(bundle);
            Assertions.assertNotNull(bundle.getModifiedDto());
            Assertions.assertFalse(BeanUtils.isDeepEqual(dto,bundle.getModifiedDto()),"Dto must differ from modifiedDto");
        });
    }
}
