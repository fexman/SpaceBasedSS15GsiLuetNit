package Model;

import java.io.Serializable;

/**
 * Created by Felix on 04.06.2015.
 */
public class AddressInfo implements Serializable {

    private static final long serialVersionUID = 1888623978750776945L;

    private String address;
    private String protocol;

    public AddressInfo(String address, String protocol) {
        this.address = address;
        setProtocol(protocol);

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        if (protocol.equalsIgnoreCase("XVSM")) {
            this.protocol = "XVSM";
        } else {
            this.protocol = "RMI";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddressInfo that = (AddressInfo) o;

        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        return !(protocol != null ? !protocol.equals(that.protocol) : that.protocol != null);

    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
        return result;
    }
}
