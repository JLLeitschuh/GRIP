package edu.wpi.grip.core;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An interface describing how an operation should be displayed in the {@link Palette} to the user.
 */
public class OperationDescription {

    /**
     * The categories that entries can be in.
     */
    public enum Category {
        IMAGE_PROCESSING,
        FEATURE_DETECTION,
        NETWORK,
        LOGICAL,
        OPENCV,
        MISCELLANEOUS,
    }

    private final String name;
    private final String summary;
    private final Category category;
    private final InputStream icon;
    private final ImmutableSet<String> aliases;

    /**
     * Private constructor - use {@link #builder} to instantiate this class.
     */
    protected OperationDescription(String name,
                                 String summary,
                                 Category category,
                                 InputStream iconStream,
                                 Set<String> aliases) {
        this.name = checkNotNull(name, "Name cannot be null");
        this.summary = checkNotNull(summary, "Summary cannot be null");
        this.category = checkNotNull(category, "Category cannot be null");
        this.icon = iconStream; // This is allowed to be null
        this.aliases = ImmutableSet.copyOf(checkNotNull(aliases, "Aliases cannot be null"));
    }

    /**
     * @return The unique user-facing name of the operation, such as "Gaussian Blur"
     */
    public String getName() {
        return name;
    }

    /**
     * @return A summary of the operation.
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @return What category the operation falls under.  This is used to organize them in the GUI
     */
    public Category getCategory() {
        return category;
    }

    /**
     * @return An {@link InputStream} of a 128x128 image to show the user as a representation of the operation.
     */
    public Optional<InputStream> getIcon() {
        return Optional.ofNullable(icon);
    }

    /**
     * @return Any old unique user-facing names of the operation. This is used to preserve compatibility with
     * old versions of GRIP if the operation name changes.
     */
    public ImmutableSet<String> getAliases() {
        return aliases;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof OperationDescription)) return false;

        OperationDescription that = (OperationDescription) o;

        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getSummary() != null ? !getSummary().equals(that.getSummary()) : that.getSummary() != null) return false;
        if (getCategory() != that.getCategory()) return false;
        if (getIcon() != null ? !getIcon().equals(that.getIcon()) : that.getIcon() != null) return false;
        return getAliases() != null ? getAliases().equals(that.getAliases()) : that.getAliases() == null;

    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getSummary() != null ? getSummary().hashCode() : 0);
        result = 31 * result + (getCategory() != null ? getCategory().hashCode() : 0);
        result = 31 * result + (getIcon() != null ? getIcon().hashCode() : 0);
        result = 31 * result + (getAliases() != null ? getAliases().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", getName())
                .add("summary", getSummary())
                .add("aliases", getAliases())
                .add("category", getCategory())
                .toString();
    }

    /**
     * Creates a new {@link Builder} instance to create a new {@code OperationDescription} object.
     * <p>
     * The created descriptor has a default category of {@link Category#MISCELLANEOUS MISCELLANEOUS} and no icon; use
     * the {@link Builder#category(Category) .category()} and {@link Builder#icon(InputStream) .icon()} methods to
     * override the default values.
     */
    public static Builder builder() {
        return new Builder()
                .category(Category.MISCELLANEOUS)
                .icon(null);
    }

    /**
     * Builder class for {@code OperationDescription}
     */
    public static final class Builder {
        private String name;
        private String summary = "PLEASE PROVIDE A DESCRIPTION TO THE OPERATION DESCRIPTION!";
        private Category category;
        private InputStream icon;
        private ImmutableSet<String> aliases = ImmutableSet.of(); // default to empty Set to avoid NPE if not assigned

        /**
         * Private constructor; use {@link OperationDescription#builder()} to create a builder.
         */
        private Builder() {
        }

        /**
         * Sets the name
         */
        public Builder name(String name) {
            this.name = checkNotNull(name);
            return this;
        }

        /**
         * Sets the summary
         */
        public Builder summary(String summary) {
            this.summary = checkNotNull(summary);
            return this;
        }

        /**
         * Sets the category
         */
        public Builder category(Category category) {
            this.category = checkNotNull(category);
            return this;
        }

        /**
         * Sets the icon
         */
        public Builder icon(InputStream icon) {
            this.icon = icon;
            return this;
        }

        /**
         * Sets the aliases
         */
        public Builder aliases(String... aliases) {
            this.aliases = ImmutableSet.copyOf(checkNotNull(aliases));
            return this;
        }

        /**
         * Builds a new {@code OperationDescription}
         */
        public OperationDescription build() {
            return new OperationDescription(
                    name,
                    summary,
                    category,
                    icon,
                    aliases);
        }
    }

}
