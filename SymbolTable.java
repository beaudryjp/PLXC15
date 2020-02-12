import java.util.*;

class SymbolTable {
    private static ArrayList<HashMap<String,String>> table;
    private static int blockIndex;

    /* Class initialization */
    static {
        table = new ArrayList<HashMap<String,String>>();
        table.add(new HashMap<String,String>());
        blockIndex = 0;
    }
	
    /* Put value */
	public static void put(String variable, String value) {
        table.get(blockIndex).put(variable, value);
	}
	
    /* Get variable from block */
	public static String get(String variable) {
		int k = 0;
        while((blockIndex >= k) && !table.get(blockIndex - k).containsKey(variable)){
            k++;
        }
        return table.get(blockIndex - k).get(variable);
	}

    /* Start a new block */
	public static void startBlock(){
        blockIndex++;
        table.add(blockIndex, new HashMap<String,String>());    
    }

    /* End the current block */
	public static void endBlock(){
        table.remove(blockIndex);
        blockIndex--; 
    }

    /* Find variable in current block */
	public static boolean findInCurrentBlock(String variable){
        if(table.get(blockIndex).containsKey(variable))
            return true;
		return false;
    }

    /* Find block corresponding to the location of the variable */
	public static int inWhichBlock(String variable){
        int k = 0;
        int tmp = blockIndex;
        while((blockIndex > k) && !table.get(blockIndex - k).containsKey(variable + "_" + tmp)){
            k++;
            tmp--;
        } 
        if(blockIndex == k) return 0;
        return tmp;
    }

    /* Check if the variable exists in any block */
	public static boolean find(String variable){
        int k = 0;
        while((blockIndex >= k) && !table.get(blockIndex - k).containsKey(variable)){
            k++;
        }

        if(blockIndex < k){
            return false;
        } else {
            return true;
        }
    }

    /* Returns the size of the array */
    public static int sizeArray(String var){
        String type = get(var);
        int indexOpen = type.indexOf("[");
        int indexClose = type.indexOf("]");
        return Integer.parseInt(type.substring(indexOpen + 1, indexClose));
    }

    /* Returns the block index */
    public static int getIndex(){
        return blockIndex;
    }

    /* Returns the string variable_blockIndex */
    public static String varBlock(String variable){
        return variable + "_" + blockIndex;
    }

    /* Dump current table */
	public static void showTable() {
		for(int k = 0; k < table.size(); k++){
			System.out.println("\tContent of the table:\n\t\tBlock index: " + k + "\n\t\tVariables: " + table.get(k).toString());
		}
	}
}