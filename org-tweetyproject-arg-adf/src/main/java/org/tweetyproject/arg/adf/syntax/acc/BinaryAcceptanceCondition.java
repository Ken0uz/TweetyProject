package org.tweetyproject.arg.adf.syntax.acc;

import java.util.Objects;
import java.util.Set;

public abstract class BinaryAcceptanceCondition implements AcceptanceCondition {

	private final AcceptanceCondition left;
	
	private final AcceptanceCondition right;

	/**
	 * @param left
	 * @param right
	 */
	public BinaryAcceptanceCondition(AcceptanceCondition left, AcceptanceCondition right) {
        if (left.equals(Objects.requireNonNull(right))) {
            throw new IllegalArgumentException("Duplicate element: " + left);
        }
		this.left = left;
		this.right = right;
	}

	@Override
	public Set<AcceptanceCondition> getChildren() {
		return Set.of(left, right);
	}
	
	public AcceptanceCondition getLeft() {
		return left;
	}
	
	public AcceptanceCondition getRight() {
		return right;
	}
	
	protected abstract String getName();

	@Override
	public int hashCode() {
		return Objects.hash(left, right);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BinaryAcceptanceCondition other = (BinaryAcceptanceCondition) obj;
		return Objects.equals(left, other.left) && Objects.equals(right, other.right);
	}
	
	@Override
	public String toString() {
		return new StringBuilder(getName())
				.append("(")
				.append(left)
				.append(",")
				.append(right)
				.append(")")
				.toString();
	}
	
}
