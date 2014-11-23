/**
 * @author Martin Melka /melkamar/
 *         Created on 22.11.14 13:55.
 */
class TransformSource {
    String filename;
    int[] includeColumns;

    TransformSource(String filename, int... includeColumns) {
        this.filename = filename;
        this.includeColumns = includeColumns;
    }
}
