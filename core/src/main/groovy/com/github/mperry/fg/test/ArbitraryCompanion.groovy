package com.github.mperry.fg.test

import fj.F
import fj.test.Arbitrary
import fj.test.Coarbitrary
import fj.test.Gen

import static fj.test.Arbitrary.arbitrary

/**
 * Created by MarkPerry on 10/01/14.
 */
class ArbitraryCompanion {

    static <A> Arbitrary<List<A>> arbJavaList(Arbitrary<A> aa) {
        def g = gen(aa.gen)
        arbitrary(g)
    }

    static <A> Gen<List<A>> gen(Gen<A> gen) {
        Gen.listOf(gen).map({ fj.data.List list ->
            list.toJavaList()
        } as F)
    }

}