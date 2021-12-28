package id.co.telkom.parser.common.charparser;

public class Predicates {
	@SuppressWarnings("unused")
	private static final class AndPredicate implements Predicate {
		private Iterable<Predicate> predicates;
		public AndPredicate(Iterable<Predicate> predicates) {
			this.predicates = predicates;
		}
		public boolean apply(Parser o) {
			for (Predicate predicate : predicates) {
				if (!predicate.apply(o)) 
					return false;
			}
			return true;
		}
	}
	@SuppressWarnings("unused")
	private static final class OrPredicate implements Predicate {
		private Iterable<Predicate> predicates;
		public OrPredicate(Iterable<Predicate> predicates) {
			this.predicates = predicates;
		}
		public boolean apply(Parser o) {
			for (Predicate predicate : predicates) {
				if (predicate.apply(o)) 
					return true;
			}
			return false;
		}
	}
	@SuppressWarnings("unused")
	private static final class EOLPredicate implements Predicate {
		public boolean apply(Parser o) {
			return o.isEOL();
		}
	}
	@SuppressWarnings("unused")
	private static final class EOFPredicate implements Predicate {
		public boolean apply(Parser o) {
			return o.isEOF();
		}
	}

	private static final class WhitespacePredicate implements Predicate {
		public boolean apply(Parser o) {
			return o.isWhiteSpace();
		}
	}
	@SuppressWarnings("unused")
	private static final class NumericPredicate implements Predicate {
		public boolean apply(Parser o) {
			return o.isNumber();
		}
	}
	@SuppressWarnings("unused")
	private static final class AlphabetPredicate implements Predicate {
		public boolean apply(Parser o) {
			return o.isAlphabet();
		}
	}
	@SuppressWarnings("unused")
	private static final class AlphaNumericPredicate implements Predicate {
		public boolean apply(Parser o) {
			return o.isAlphaNumeric();
		}
	}
	@SuppressWarnings("unused")
	private static final class BeginningOfLinePredicate implements Predicate {
		public boolean apply(Parser o) {
			return o.getColumn() == 0;
		}
	}

	public static final Predicate WHITESPACE = new WhitespacePredicate();

}
