package e_learning.services;


import e_learning.entity.Validation;

public interface NotificationService {
    public void envoyerEmailVerificationUser(Validation validation);
}
