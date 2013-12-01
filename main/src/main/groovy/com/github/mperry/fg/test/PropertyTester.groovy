package com.github.mperry.fg.test

import fj.F
import fj.F2
import fj.F3
import fj.F4
import fj.F5
import fj.P2
import fj.data.Option
import fj.test.Arbitrary
import fj.test.Bool
import fj.test.CheckResult
import fj.test.Property
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import org.codehaus.groovy.runtime.NullObject
import org.junit.Assert

import static fj.test.Arbitrary.*

/**
 * Created with IntelliJ IDEA.
 * User: MarkPerry
 * Date: 30/11/13
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
@TypeChecked
class PropertyTester {

	static int maxArgs = 5

	final static Map NULLABLE_INTEGER = [(Integer.class): Arbitrary.arbNullableInteger()]

	final static Map<Class<?>, Arbitrary> defaultMap = [
			(BigDecimal.class): arbBigDecimal,
			(BigInteger.class): arbBigInteger,
			(BitSet.class): arbBitSet,
			(Boolean.class): arbBoolean,
			(Byte.class): arbByte,
			(Calendar.class): arbCalendar,
			(Character.class): arbCharacterBoundaries,
			(Date.class): arbDate,
			(Double.class): arbDoubleBoundaries,
			(Float.class): arbFloatBoundaries,
			(Integer.class): arbIntegerBoundaries,
			(Long.class): arbLongBoundaries,
			(String.class): arbString,

			(ArrayList.class): arbArrayList(arbIntegerBoundaries),
			(java.util.List.class): arbArrayList(arbIntegerBoundaries)
	]

	static Property createProp(Closure<Boolean> c) {
		createProp(defaultMap, c)
	}

	static Property createProp(Map<Class<?>, Arbitrary> map, Closure<Boolean> c) {
		def list = c.getParameterTypes()
		def arbOpts = list.collect { Class it -> map.containsKey(it) ? Option.some(map[it]) : Option.none() }
		def allMapped = arbOpts.forAll { Option it -> it.isSome() }
		if (!allMapped) {
			throw new Exception("Not all types of closure parameters were mapped")
		}
		createProp(arbOpts.collect { Option<Arbitrary> it -> it.some() }, c)
	}

	static Property createProp(Map<Class<?>, Arbitrary> map, Closure<Boolean> pre, Closure<Boolean> c) {
		def list = c.getParameterTypes()
		def arbOpts = list.collect { Class it -> map.containsKey(it) ? Option.some(map[it]) : Option.none() }
		def allMapped = arbOpts.forAll { Option it -> it.isSome() }
		if (!allMapped) {
			throw new Exception("Not all function parameter types were found: ${list.findAll { !map.containsKey(it)}}")
		}
		createProp(arbOpts.collect { Option<Arbitrary> it -> it.some() }, pre, c)
	}

	static CheckResult showAllWithMap(Boolean ok, Map<Class<?>, Arbitrary> map, Closure<Boolean> c) {
		def p = createProp(map, c)
		p.checkBooleanWithNullableSummary(ok)
//		def cr = p.check()
//		CheckResult.summary.println(cr)
//		Assert.assertTrue(cr.isOk() == ok)
//		cr
	}

	static CheckResult showAllWithMap(Boolean ok, Map<Class<?>, Arbitrary> map, Closure<Boolean> pre, Closure<Boolean> c) {
		def p = createProp(map, pre, c)
		def cr = p.check()
		p.checkBooleanWithNullableSummary(ok)
	}

	static CheckResult showAllWithMap(Boolean ok, Map<Class<?>, Arbitrary> map, Option<Closure<Boolean>> pre, Closure<Boolean> c) {
		def p = pre.isSome() ? createProp(map, pre.some(), c) : createProp(map, c)
//		def p = createProp(map, pre, c)
		def cr = p.check()
		p.checkBooleanWithNullableSummary(ok)
	}


	/**
	 *
	 * @param map Override the default map
	 * @param c
	 */
	static CheckResult showAll(Map<Class<?>, Arbitrary<?>> map, Closure<Boolean> c) {
		showAllWithMap(true, defaultMap + map, c)
	}

	static CheckResult showAll(Boolean ok, Map<Class<?>, Arbitrary> map, Closure<Boolean> pre, Closure<Boolean> c) {
		showAllWithMap(ok, defaultMap + map, pre, c)
	}

	static CheckResult showAll(Boolean ok, Map<Class<?>, Arbitrary> map, Closure<Boolean> c) {
		showAllWithMap(ok, defaultMap + map, c)
	}

	static CheckResult showAll(TestConfig config) {
		if (config.pre.isSome()) {
			showAllWithMap(config.truth, config.map, config.pre.some(), config.function)
		} else {
			showAllWithMap(config.truth, config.map, config.function)
		}

	}

	static CheckResult showAll(Closure<Boolean> c) {

		showAllWithMap(true, defaultMap, c)
	}

	static CheckResult showAll(Closure<Boolean> pre, Closure<Boolean> c) {
		showAllWithMap(true, defaultMap, pre, c)
	}

	static CheckResult showAll(Map<Class<?>, Arbitrary> map, Closure<Boolean> pre, Closure<Boolean> c) {
		showAllWithMap(true, defaultMap + map, pre, c)
	}


	static CheckResult showAll(Boolean ok, Closure pre, Closure c) {
		showAllWithMap(ok, defaultMap, pre, c)
	}

	@TypeChecked(TypeCheckingMode.SKIP)
	static Property createProp(List<Arbitrary> list, Closure<Boolean> c) {
		if (c.getMaximumNumberOfParameters() > maxArgs) {
			throw new Exception("Testing does not support ${c.getMaximumNumberOfParameters()}, maximum supported is $maxArgs")
		}
		this."createProp${list.size()}"(list, c)
	}

	@TypeChecked(TypeCheckingMode.SKIP)
	static Property createProp(List<Arbitrary> list, Closure<Boolean> pre, Closure<Boolean> c) {
		if (c.getMaximumNumberOfParameters() > maxArgs) {
			throw new Exception("Testing does not support ${c.getMaximumNumberOfParameters()}, maximum supported is $maxArgs")
		}
		this."createProp${list.size()}"(list, Option.some(pre), c)
	}

	static Property implies(Boolean pre, Boolean result) {
		Bool.bool(pre).implies(result)
	}

	static Property createProp0(List<Arbitrary> list, Closure<Boolean> c) {
		createProp0(list, { -> true }, c)
	}

	static Property createProp0(List<Arbitrary> list, Closure<Boolean> pre, Closure<Boolean> c) {
		def preOk = pre.call()
		def result = !preOk ? true: c.call()
		implies(preOk, result)
	}

	@TypeChecked
	static Property createProp1(List<Arbitrary<?>> list, Closure<Boolean> closure) {
		createProp1(list, { a -> true }, closure)
	}

	@TypeChecked
	static Property createProp1(List<Arbitrary<?>> list, Closure<Boolean> pre, Closure<Boolean> closure) {
		Property.property(list[0], { a ->
			def preOk = pre.call(a)
			def result = !preOk ? true : closure.call(a)
			implies(preOk, result)
		} as F)
	}

//	@TypeChecked
	@TypeChecked(TypeCheckingMode.SKIP)
	static Property createProp2(List<Arbitrary<?>> list, Closure<Boolean> closure) {
			createProp2(list, Option.<Closure<Boolean>>none(), closure)
	}

	@TypeChecked
	static Property createProp2(List<Arbitrary<?>> list, Option<Closure<Boolean>> pre, Closure<Boolean> closure) {
		Property.property(list[0], list[1], { Object a, Object b ->
			def preOk = pre.map { Closure<Boolean> it -> it.call(a, b) }.orSome(true)
//			def preOk = pre.call(a, b)
			// is a and b of type closure param 1 and 2?
			def objectTypes = [a.getClass(), b.getClass()]
			def closureTypes = closure.getParameterTypes().toList()
			def typesOk = objectTypes.zip(closureTypes).inject(true) { Boolean result, P2<Class, Class> p ->
				result && ((p._1() == NullObject.class) ? true : p._2().isAssignableFrom(p._1()))
			}
			if (!typesOk || objectTypes.size() != closureTypes.size()) {
				println("Cannot call closure with value types $objectTypes.  Closure requires types $closureTypes")
				return Property.prop(false)
			}

			try {
				def result = !preOk ? true : closure.call(a, b)
				implies(preOk, result)
			} catch (Exception e) {
				println e.getMessage()
				Property.prop(false)
			} catch (Throwable e) {
				println e.getMessage()
				Property.prop(false)
			} catch (RuntimeException e) {
				println e.getMessage()
				Property.prop(false)
			}
		} as F2)
	}

	@TypeChecked
	static Property createProp3(List<Arbitrary<?>> list, Closure<Boolean> closure) {
		createProp3(list, { a, b, c -> true }, closure)
	}


	@TypeChecked
	static Property createProp3(List<Arbitrary<?>> list, Closure<Boolean> pre, Closure<Boolean> closure) {
		Property.property(list[0], list[1], list[2], { a, b, c ->
			def preOk = pre.call(a, b, c)
			def result = !preOk ? true : closure.call(a, b, c)
			implies(preOk, result)
		} as F3)
	}

	@TypeChecked
	static Property createProp4(List<Arbitrary<?>> list, Closure<Boolean> closure) {
		Property.property(list[0], list[1], list[2], list[3], { a, b, c, d ->
			Property.prop(closure.call(a, b, c, d))
		} as F4)
	}

	@TypeChecked
	static Property createProp5(List<Arbitrary<?>> list, Closure<Boolean> closure) {
		Property.property(list[0], list[1], list[2], list[3], list[4], { a, b, c, d, e ->
			Property.prop(closure.call(a, b, c, d, e))
		} as F5)
	}

}