

package edu.rit.ibd.a3;

import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

	public class TNFDecomposition {

		public static void main(String[] args) throws Exception {
		final String relation = args[0];
		final String fdsStr = args[1];
		final String cksStr =args[2];
		final String outputFile = args[3];
//
			// This stores the attributes of the input relation.
			Set<String> attributes = new HashSet<>(parseAttributes(relation));
			Map<Set<String>, Set<String>> fdInOrder = new HashMap<>(parseFDS(fdsStr));
			System.out.println("kfdddddd = " + fdInOrder);


			//TODO: parse cksStr
//		for (Set<String> set:parseCksStr(cksStr))
//			System.out.println(set);
			Set<Set<String>> parsedCKS = new HashSet<>(parseCksStr(cksStr)); //cks parsed

			// This stores the functional dependencies provided as input.
//		Set<Object> fds = new HashSet<>();
			// This stores the candidate keys provided as input.
//		List<Set<String>> cks = new ArrayList<>();
			// This stores the final 3NF decomposition, i.e., the output.
			List<Set<String>> decomposition = new ArrayList<>();


			// TODO 0: Your code here!
			//
			// Parse the input relation that include its attributes. Recall that relation and attribute names can be formed by multiple letters.
			//
			// Parse the input functional dependencies. These are already a canonical cover. Recall that attributes can be formed by multiple letters.
			//
			// Parse the input candidate keys. Recall that attributes can be formed by multiple letters.
			//
			// Analyze whether the relation is already in 3NF:
			//	alreadyIn3NF=true
			//	For each FD A->B (A and B are sets of attributes):
			//		check = (B is included or equal in A) OR (A is superkey) OR (B \ A is contained in at least one candidate key)
			//		If !check: alreadyIn3NF=false, proceed to decompose!

			/**
			 * Checking if decomposition is required
			 * */

			boolean needToDecompose = true;
			List<Set<String>> newDecomposition = new ArrayList<>();
//		System.out.println(needDecomposition(fdInOrder,parsedCKS));

			System.out.println("62 " + fdInOrder);
			System.out.println("63 " + parsedCKS);

			if (!needDecomposition(fdInOrder,parsedCKS)){
				//FIXME: may assume wrong
				needToDecompose = false;

			}
			if (needToDecompose){
				for (Map.Entry<Set<String>, Set<String>> en: fdInOrder.entrySet()){
					System.out.println("kwdcjwdb");
					System.out.println();
					Set<String> createdRelation = new HashSet<>(creatingRelation(en.getValue(), en.getKey()));
					decomposition.add(createdRelation);
				}				System.out.println("76 " + decomposition);

				newDecomposition.addAll(CheckCKPresentAndRemoveRedundant(decomposition,parsedCKS));
			}
			else {
//			for (Map.Entry<Set<String>, Set<String>> map: fdInOrder.entrySet()){
//				Set<String> set1 = new HashSet<>(map.getKey());
//				set1.addAll(map.getValue());
//				decomposition.add(set1);
//				newDecomposition.addAll(removeRedundant(decomposition));
//			}
				newDecomposition.add(attributes);
			}
			//TODO: uncomment from here
			System.out.println("rrr  " + newDecomposition);
			PrintWriter writer = new PrintWriter(new File(outputFile));
			for (Set<String> r : newDecomposition)
				writer.println("r(" + r.stream().sorted().collect(java.util.stream.Collectors.toList()).
						toString().replace("[", "").replace("]", "") + ")");
			writer.close(); //TODO: till here
		}

		/**
		 * Attributes parsing
		 * */
		public static Set<String> parseAttributes(String relation){ //r(A,B,C)
			Set<String> attributesParsed = new HashSet<>();
			int firstBracket = relation.indexOf("(");
			int secondBracket = relation.indexOf(")");
			String toParse = relation.substring(firstBracket+1,secondBracket);
			String[] toStore = toParse.split(",");
			for (String str:toStore){
				String store = str.trim();
				attributesParsed.add(store);
			}
			System.out.println("attributes");

			return  attributesParsed;
		}
		/**
		 * fds parsing
		 * */
		public static Map<Set<String>,Set<String>> parseFDS(String fdStr){ // A -> B;B -> C
			// A -> B
			// B -> C
			// A , B
			boolean flag = false;
			Map<Set<String>,Set<String>> fds = new HashMap<>();
			String [] str = fdStr.split(";");
			List<ArrayList<String>> listToput = new ArrayList<ArrayList<String>>();
			Set<Set<String>> rightListCopy = new HashSet<>();
			for (String i:str){
				String trimmed = i.trim();
				String[] splitOnArrow;
				splitOnArrow = trimmed.split("->");
				String [] toMakeListForRight = splitOnArrow[1].split(",");//this goes as key
				String[] toMakeListForLeft = splitOnArrow[0].split(",");
				Set<String> leftList = new HashSet<>();
				for (String j:toMakeListForLeft){
					leftList.add(j.trim());
				}

				Set<String> rightList = new HashSet<>();
				for (String k:toMakeListForRight){
					rightList.add(k.trim());
				}
				if (rightListCopy.contains(rightList)) {
					flag = true;
				}
				rightListCopy.add(rightList);
				if (flag){
					Set<String> newSet = new HashSet<>();
					leftList.addAll(fds.get(rightList));
					newSet.addAll(leftList);
					fds.put(rightList,newSet);

				}
				else {
					fds.put(rightList,leftList);
				}

			}
			System.out.println("fds = " + fds);
			return fds;
		}

		/**
		 * parsing cks
		 * */

		public  static  Set<Set<String>> parseCksStr(String cksStr){
			Set<Set<String>> retValue = new HashSet<>();
			String[] store = cksStr.split(";");
			for (String str:store){
				Set<String> intermidiate = new HashSet<>();
				String[] storeAgain = str.split(",");
				for (String intrem:storeAgain){
					intermidiate.add(intrem.trim());
				}
				retValue.add(intermidiate);
			}
			System.out.println("ck");
			return retValue;
		}


		/**
		 * checking if decomposition is needed
		 * */
		public static boolean needDecomposition(Map<Set<String>, Set<String>> fdInOrder, Set<Set<String>> cks){
			//	For each FD A->B (A and B are sets of attributes):
			//		check = (B is included or equal in A) OR (A is superkey) OR (B \ A is contained in at least one candidate key)
			//		If !check: alreadyIn3NF=false, proceed to decompose!
			boolean flag = true;
			for (Map.Entry<Set<String>, Set<String>> entry:fdInOrder.entrySet()){
				System.out.println(fdInOrder);
				System.out.print(entry.getValue() + "->");
				System.out.print(entry.getKey());
				for (Set<String> sets:cks){
					System.out.println("cks = " + sets);
					System.out.println("174 " + entry.getValue());
					System.out.println("175 " + sets);
					if (entry.getValue().containsAll(entry.getKey()) || entry.getValue().containsAll(sets) || sets.containsAll(entry.getKey())){
						flag = false;
						break;

					}
					else flag = true;

				}
				if (flag)
					break;
			}
			System.out.println("need to dcmpose ");
			return flag;
		}

		/**
		 * creating relation
		 * */

		public static Set<String> creatingRelation(Set<String> lhs, Set<String> rhs){
			Set<String> result = new HashSet<>();
			result.addAll(lhs);
			result.addAll(rhs);
			System.out.println("printing");
			System.out.println(result + "re");
			return result;
		}
		public static List<Set<String>> CheckCKPresentAndRemoveRedundant(List<Set<String>> decomposition, Set<Set<String>> cks){
			boolean ckWasPresent = false;
			List<Set<String>> decompositionCopy = new ArrayList<>(decomposition);
			Set<String> ckToAdd = new HashSet<>();
			for (Set<String> set:cks){
				ckToAdd.addAll(set);
				break;
			}

//		for (Set<String> dcmp:decomposition){
//			for (Set<String> ck:cks){
//				ckToAdd.addAll(ck);
//				if (dcmp.containsAll(ck)){
//					ckWasPresent = true;
//					break;
//				}
//			}
////			if (!ckWasPresent){
////				decompositionCopy.add(ckToAdd);
////			}
//		}

			for (Set<String> dcmp:decomposition){
				for (Set<String> ck:cks){
					if (dcmp.containsAll(ck)){
						ckWasPresent = true;
					}

				}
			}
			System.out.println("234 " + ckToAdd);
			if (!ckWasPresent){
				decompositionCopy.add(ckToAdd);
			}
			System.out.println("check CK");
			System.out.println("decomsn = " + decompositionCopy);

			return removeRedundant(decompositionCopy);
		}
		public static List<Set<String>> removeRedundant(List<Set<String>> newDecomposition){
			List<Set<String>> copy = new ArrayList<>();
			for (Set<String> set:newDecomposition){
				Set<String> newSet = new HashSet<>();
				for (String str:set){
					newSet.add(str.trim());
				}
				copy.add(newSet);
			}
			List<Set<String>> result = new ArrayList<>(copy);
			for (Set<String> X : copy){
				for (Set<String> Y : copy){
					if (X.size() <= Y.size() && Y.containsAll(X) && !Y.equals(X)){
						result.remove(X);
					}

				}
			}
			System.out.println("remove redundant");
			return result;
		}
	}


