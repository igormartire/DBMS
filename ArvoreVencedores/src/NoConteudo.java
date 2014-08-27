
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
    

    public void salva(DataOutputStream out) throws IOException {
        info.salva(out);
    }

    void atualizar() throws IOException{
        info = Cliente.le(in);
    }
    
}
