package com.gircenko.gabriel.run4est.models;

/**
 * Created by Gabriel Gircenko on 28-Oct-16.
 */

public class JogModelWithId {

    private JogModel jog;
    private String jogId;

    public JogModelWithId(JogModel jog, String jogId) {
        this.jog = jog;
        this.jogId = jogId;
    }

    public JogModel getJog() {
        return jog;
    }

    public String getJogId() {
        return jogId;
    }

    public void setJog(JogModel jog) {
        this.jog = jog;
    }

    public void setJogId(String jogId) {
        this.jogId = jogId;
    }
}
