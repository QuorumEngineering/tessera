package com.quorum.tessera.p2p;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OpenPojoTest {

    public OpenPojoTest() {
    }

    @Test
    public void executeOpenPojoValidations() {

        Validator pojoValidator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new SetterTester())
                .with(new GetterTester())
                .build();

        
        pojoValidator.validate(PojoClassFactory.getPojoClass(ResendRequest.class));

    }

    

    
}
