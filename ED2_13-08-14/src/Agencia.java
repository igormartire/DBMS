import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Agencia {
    private int cod;
    private String nome;
    private String gerente;

    public Agencia(){}

    public Agencia(int cod, String nome, String gerente) {
        this.cod = cod;
        this.nome = nome;
        this.gerente = gerente;
    }

    public int getCod() {
        return cod;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGerente() {
        return gerente;
    }

    public void setGerente(String gerente) {
        this.gerente = gerente;
    }

    public void salva(DataOutputStream out) throws IOException {
        out.writeInt(this.cod);
        out.writeUTF(this.nome);
        out.writeUTF(this.gerente);
    }
    
    public void le(DataInputStream in) throws IOException {
        this.cod = in.readInt();
        this.nome = in.readUTF();
        this.gerente = in.readUTF();
    }
    
}
