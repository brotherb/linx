package linx

import org.junit.Test
import org.junit.Assert._

class LinxTest {
  @Test
  def empty{
    val path = "/"
    assertEquals(Root(), path)
    val Root() = path
  }

  @Test
  def variable{
    val X = Root / *
    val path = "/x"
    val X(x) = path
    assertEquals("x", x)
    assertEquals(X(x), path)
  }

  def variableMulti{
    val X = Root / * / * / *
    val path = "/a/b/c"
    val X(a,b,c) = path
    assertEquals("a", a)
    assertEquals("b", b)
    assertEquals("c", c)
    assertEquals(path, X(a,b,c))
  }

  @Test
  def literal{
    val X = Root / "A" / "B" / "C"
    val path = "/A/B/C"
    assertEquals(path, X())
    val X() = path
  }

  @Test
  def mixed{
    val X = Root / "A" / * / "C" / "D" / * / * / "G" / * / "I"
    val path = "/A/B/C/D/E/F/G/H/I"
    val X(b, e, f, h) = path
    assertEquals("B", b)
    assertEquals("E", e)
    assertEquals("F", f)
    assertEquals("H", h)
    assertEquals(path, X(b, e, f, h))
  }

  @Test
  def failRoot{
    assertFalse(Root.unapply("/a"))
  }

  @Test
  def failLiteral{
    val A = Root / "A"
    assertFalse(A.unapply("/"))
    assertFalse(A.unapply("/B"))
    assertFalse(A.unapply("/A/B"))
  }

  @Test
  def failVariable{
    val A = Root / *
    assertEquals(None, A.unapply("/"))
    assertEquals(None, A.unapply("/A/B"))
  }

  @Test
  def failMixed{
    val A = Root / * / "A"
    assertEquals(None, A.unapply("/"))
    assertEquals(None, A.unapply("/A/B"))
    assertEquals(None, A.unapply("/B/A/C"))
  }

  @Test
  def literalComposite {
    val AB = Root / "A" | Root / "B"
    assertEquals(AB(), "/A")
    val AB() = "/A"
    val AB() = "/B"
  }

  @Test
  def variableComposite {
    val AB = Root / * / "A" | Root / "A" / *
    assertEquals(AB("B"), "/B/A")
    val AB("B") = "/B/A"
    val AB("B") = "/A/B"
  }

  @Test
  def literalOnUnion {
    val ABC = (Root / * / "A" | Root / "A" / *) / "C"
    assertEquals(ABC("B"), "/B/A/C")
    val ABC("B") = "/B/A/C"
    val ABC("B") = "/A/B/C"
  }

  @Test
  def variableOnUnion {
    val ABX = (Root / * / "A" | Root / "A" / *) / *
    assertEquals(ABX("X", "Y"), "/X/A/Y")
    val ABX("X", "Y") = "/X/A/Y"
    val ABX("X", "Y") = "/A/X/Y"
  }

  @Test
  def unionsInUnions {
    val ABX = (Root / * / "A" | Root / "A" / *) / *
    val BYZ = (Root / "Y" / "Y" / "Y" / * | Root / "Y" / *) / "Z" / * / "Z"
    val XXX = (ABX | BYZ) / "U" / *
    assertEquals(XXX("x", "y", "z"), "/x/A/y/U/z")
    val XXX("x", "y", "z") = "/x/A/y/U/z"
    val XXX("x", "y", "z") = "/A/x/y/U/z"
  }

  @Test
  def unionBacktracking {
    val X = (Root | Root / "a") / *
    val X("b") = "/a/b"
  }
}
