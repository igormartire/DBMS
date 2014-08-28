
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Lucas
 */
public class NoConteudo {
    Cliente info;
    DataInputStream in;
    

    //Escreve o registro da entidade no arquivo passado
    public void salva(DataOutputStream out) throws IOException {
        info.salva(out);
    }

    //Atualiza seu atributo info com o próximo registro do arquivo
    void atualizar() throws IOException{
        info = Cliente.le(in);
    }
    
}
