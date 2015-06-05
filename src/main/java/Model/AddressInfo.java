package Model;

import java.io.Serializable;

/**
 * Created by Felix on 04.06.2015.
 */
public class AddressInfo implements Serializable {

    private static final long serialVersionUID = 1888623978750776945L;

    private String address;
    private Protocol protocol;

    public AddressInfo(String address, Protocol protocol) {
        setAddress(address);
        setProtocol(protocol);

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public enum Protocol {

        XVSM("XVSM"),
        RMI("RMI");

        private final String text;

        /**
         * @param text
         */
        Protocol(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }

        public static Protocol byName(String name) {
            if (name.equalsIgnoreCase(XVSM.toString())) {
                return XVSM;
            } else if (name.equalsIgnoreCase(RMI.toString())) {
                return RMI;
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return "AddressInfo{" +
                "address='" + address + '\'' +
                ", protocol=" + protocol +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddressInfo that = (AddressInfo) o;

        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        return protocol == that.protocol;

    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
        return result;
    }
}
