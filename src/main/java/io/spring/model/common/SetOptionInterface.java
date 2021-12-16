package io.spring.model.common;

/**
 * optionNm1,2를 설정해줄 필요가 있는 객체들이 상속받게 해 optionNm 정하는 과정을 추상화하는 함수를 사용하기 위한 인터페이스
 */
public interface SetOptionInterface {
    void setOptionNm1(String optionNm);
    void setOptionNm2(String optionNm);
    void setOptionNm3(String optionNm);
}
