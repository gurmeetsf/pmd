/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.property.PropTestConfig
import io.kotest.property.checkAll
import io.kotest.property.forAll
import net.sourceforge.pmd.lang.java.types.testdata.LubTestData
import net.sourceforge.pmd.lang.java.types.testdata.LubTestData.*
import java.io.Serializable

/**
 * @author Clément Fournier
 */
class GlbTest : FunSpec({

    with(TypeDslOf(testTypeSystem)) { // import construction DSL
        with(gen) { // import constants

            test("Test intersection minimization") {

                forAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    canIntersect(t, s) and t.isSubtypeOf(s) implies {
                        glb(t, s) == t
                    }
                }

                // in particular
                checkAll(ts.allTypesGen) { t ->
                    glb(t, t) shouldBe t // regardless of what kind of type t is
                }
            }

            test("Test intersection symmetry") {

                checkAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    if (canIntersect(t, s)) {
                        glb(t, s) shouldBe glb(s, t)
                    }
                }
            }

            test("Test intersection left associativity") {

                checkAll(PropTestConfig(-1697903442944791680), ts.allTypesGen, ts.allTypesGen, ts.allTypesGen) { t, s, r ->
                    if (canIntersect(t, s, r)) {
                        glb(glb(t, s), r) shouldBe glb(t, s, r)
                    }
                }
            }

            test("Test intersection right associativity") {

                checkAll(ts.allTypesGen, ts.allTypesGen, ts.allTypesGen) { t, s, r ->
                    if (canIntersect(t, s, r)) {
                        glb(t, glb(s, r)) shouldBe glb(t, s, r)
                    }
                }
            }

            test("Test GLB min") {

                glb(ts.SERIALIZABLE, t_ArrayList) shouldBe t_ArrayList
                glb(t_ArrayList, ts.SERIALIZABLE) shouldBe t_ArrayList

            }
        }
    }

})
