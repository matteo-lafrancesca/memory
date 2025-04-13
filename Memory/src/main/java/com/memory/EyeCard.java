package com.memory;

import java.util.List;

public class EyeCard extends Card {
    public EyeCard(int id, String image) {
        super(id, image, CardType.SPECIAL);
    }

    @Override
    public void onMatch(GameManager gameManager) {
        // Déclencher l'effet Eye dans le GameManager
        gameManager.activateEyeEffect();
        System.out.println("Eye activé ! 5 cartes non appariées seront temporairement révélées.");
    }
}