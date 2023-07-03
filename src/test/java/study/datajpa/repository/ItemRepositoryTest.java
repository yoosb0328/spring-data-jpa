package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ItemRepositoryTest {
    @Autowired ItemRepository itemRepository;
    @Test
    public void save() {
        //테스트하기 전에 SimpleJpaRepository의 save()에 중단점 찍고
        // memberController의 @PostConstruct init()을 주석처리하고 테스트 할 것
        Item item = new Item("a");
        itemRepository.save(item);
    }

}