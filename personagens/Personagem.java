public abstract class Personagem {
    private String nome;
    private int pontosDeVida = 100;
    private int forcaDeAtaque;
    private int forcaDeDefesa;
    private int alcanceDeAtaque;
    private int posicaoX;
    private int posicaoY;
 


    public Personagem(String nome, int forcaDeAtaque, 
                     int forcaDeDefesa, int alcanceDeAtaque) {
        this.nome = nome;
        this.forcaDeAtaque = forcaDeAtaque;
        this.forcaDeDefesa = forcaDeDefesa;
        this.alcanceDeAtaque = alcanceDeAtaque;
    }

    
    public abstract void usarPoderEspecial();
     public abstract String getTipo();


    public void mover(int novaX, int novaY) {
        this.posicaoX = novaX;
        this.posicaoY = novaY;
    }

     public void receberDano(int dano) {
        this.pontosDeVida -= dano;
        if (this.pontosDeVida < 0) {
            this.pontosDeVida = 0;
        }
    }


    public int calcularDistancia(Personagem outro) {
        int diffX = Math.abs(this.posicaoX - outro.getPosicaoX());
        int diffY = Math.abs(this.posicaoY - outro.getPosicaoY());
        return Math.max(diffX, diffY);
    }

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

    public int getForcaDeDefesa() {
        return forcaDeDefesa;
    }

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


}