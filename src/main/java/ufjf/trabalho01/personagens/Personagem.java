package ufjf.trabalho01.personagens;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import ufjf.trabalho01.GridManager;

public abstract class Personagem {
    protected String nome;
    protected int pontosDeVida = 100;
    protected int forcaDeAtaque;
    protected int pontosDeDefesa;
    protected final int pontosDeDefesaMax;
    protected int alcanceDeAtaque;
    protected final int defesaBase;
    protected int posicaoX;
    protected int posicaoY;
    protected String tipo;
    protected Node view;

    public Personagem(String tipo, String nome, int forcaDeAtaque,
                     int forcaDeDefesa, int alcanceDeAtaque) {
        this.tipo = tipo;
        this.nome = nome;
        this.forcaDeAtaque = forcaDeAtaque;

        this.pontosDeDefesaMax = forcaDeDefesa;
        this.pontosDeDefesa = forcaDeDefesa;

        this.alcanceDeAtaque = alcanceDeAtaque;
        this.defesaBase     = forcaDeDefesa;
        this.posicaoY = -1;
        this.posicaoX = -1;
        this.view = criarView();
    }

    public String getTipo() {
         return this.tipo;
    }

    protected Node criarView() {
        Circle circle = new Circle(16);
        circle.setFill(Color.RED);
        circle.setStroke(Color.BLACK);
        return circle;
    }

    public void restoreDefense() {
        this.pontosDeDefesa = this.pontosDeDefesaMax;
    }

    public void sofrerDano(int danoBruto) {
        int danoAbsorvidoPelaDefesa = Math.min(danoBruto, this.pontosDeDefesa);
        this.pontosDeDefesa -= danoAbsorvidoPelaDefesa;

        int danoRestante = danoBruto - danoAbsorvidoPelaDefesa;
        this.pontosDeVida -= danoRestante;

        if (this.pontosDeVida < 0) {
            this.pontosDeVida = 0;
        }
    }

    public int calcularDistancia(Personagem oponente) {
        int dx = Math.abs(this.posicaoX - oponente.posicaoX);
        int dy = Math.abs(this.posicaoY - oponente.posicaoY);
        return Math.max(dx, dy);
    }

    public abstract String usarPoderEspecial(Personagem oponente);

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public int getPontosDeVida() {
        return pontosDeVida;
    }

    public int getForcaDeAtaque() {
        return forcaDeAtaque;
    }

    public int getPontosDeDefesa() { return pontosDeDefesa; }
    public int getPontosDeDefesaMax() { return pontosDeDefesaMax; }

    public int getAlcanceDeAtaque() {
        return alcanceDeAtaque;
    }

    public int getPosicaoX() {
        return posicaoX;
    }

    public int getPosicaoY() {
        return posicaoY;
    }

    public void setPosicaoX(int posicaoX) {
        this.posicaoX = posicaoX;
    }

    public void setPosicaoY(int posicaoY) {
        this.posicaoY = posicaoY;
    }

    public void setAlcanceDeAtaque(int alcance) {
        this.alcanceDeAtaque = alcance;
    }

    public Node getView() {
        return this.view;
    }
}