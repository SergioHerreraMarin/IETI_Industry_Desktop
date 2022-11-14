public abstract class InterfaceComponentData {
    
    private String id;
    private String block;
    private String label;

    public InterfaceComponentData(String id, String block, String label) {
        this.id = id;
        this.block = block;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBlock() {
        return block;
    }

    public String getLabel() {
        return label;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    @Override
    public String toString() {
        return "InterfaceComponentData [id=" + id + "]";
    }

}
