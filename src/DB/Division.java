package DB;

import java.io.Serializable;

/**
 * Created by Anil on 19/03/2018
 */
public class Division implements Serializable {
    private String name;
    private CSClass csClass;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CSClass getCsClass() {
        return csClass;
    }

    public void setCsClass(CSClass csClass) {
        this.csClass = csClass;
    }
}
