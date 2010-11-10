package net.sf.xfresh.dbe;

/**
 * Date: Nov 10, 2010
 * Time: 9:31:34 PM
 *
 * @author Nikolay Malevanny nmalevanny@yandex-team.ru
 */
public class CheckDbResult {
    private int status;
    private String description;
    private String checkDescription;

    public CheckDbResult(final int status, final String description, final String checkDescription) {
        this.status = status;
        this.description = description;
        this.checkDescription = checkDescription;
    }

    public int getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getCheckDescription() {
        return checkDescription;
    }
}
