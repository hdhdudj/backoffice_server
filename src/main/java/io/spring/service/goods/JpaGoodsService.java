package io.spring.service.goods;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.dao.goods.jparep.JpaItasrdRepository;
import io.spring.dao.goods.jparep.JpaItasrtRepository;
import io.spring.dao.goods.jparep.JpaItvariRepository;
import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.Itasrd;
import io.spring.model.goods.Itasrt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class JpaGoodsService {
    @Autowired
    private JpaItasrtRepository jpaItasrtRepository;
    @Autowired
    private JpaItvariRepository jpaItvariRepository;
    @Autowired
    private MyBatisCommonDao myBatisCommonDao;
    @Autowired
    private JpaItasrdRepository jpaItasrdRepository;

    public List<Itasrt> findAll() {
        List<Itasrt> goods = new ArrayList<>();
        jpaItasrtRepository.findAll().forEach(e -> goods.add(e));
        return goods;
    }

    public Optional<Itasrt> findById(Long goodsId) {
        Optional<Itasrt> goods = jpaItasrtRepository.findById(goodsId);
        return goods;
    }

    public void deleteById(Long goodsId) {
        jpaItasrtRepository.deleteById(goodsId);
    }

    public long save(Itasrt goods) {
        HashMap<String, Object> arr = new HashMap<String, Object>();

        arr.put("seqName", "seq_ITASRT");
        HashMap<String, Object> x1 = myBatisCommonDao.getSequence(arr);
        goods.setAssortId((long)x1.get("nextval"));

        jpaItasrtRepository.save(goods);

        return goods.getAssortId();
    }

    public Itasrd save(Itasrd goods) {
        HashMap<String, Object> arr = new HashMap<String, Object>();

        arr.put("seqName", "assort_id_ITASRD");
        HashMap<String, Object> x1 = myBatisCommonDao.getSequence(arr);
        goods.setAssortId((long)x1.get("nextval"));

        arr.put("seqName", "seq_ITASRD");
        HashMap<String, Object> x2 = myBatisCommonDao.getSequence(arr);
        goods.setSeq(Long.valueOf((long)x2.get("nextval")).intValue());

        jpaItasrdRepository.save(goods);

        return goods;
    }

    public void saveItvariList(GoodsRequestData goodsRequestData) {
        for (GoodsRequestData.Attributes item : goodsRequestData.getAttributes()) {
            if(item.getSize().size() > 0){ // size 목록일 경우
            }
            else if(item.getColor().size() > 0){ // color 목록일 경우

            }
        }

//        jpaItvariRepository.save(goods);
    }

    public void updateById(Long goodsId, Itasrt goods) {
        Optional<Itasrt> e = jpaItasrtRepository.findById(goodsId);
        if (e.isPresent()) {
            e.get().setAssortId(goods.getAssortId());
            e.get().setAssortNm(goods.getAssortNm());
            jpaItasrtRepository.save(goods);
        }
    }
}
