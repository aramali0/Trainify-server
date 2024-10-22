package e_learning.services;


import e_learning.entity.UserApp;
import e_learning.entity.Validation;

public interface ValidationService {
    public Validation addNewValidation(UserApp userApp);

    public Validation getValidationBuCode(String code);
}
