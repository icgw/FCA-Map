FCA-Map: Identifying Mappings by Formal Concept Analysis
========================================================

![Workflow Status](https://github.com/icgw/FCA-Map/workflows/Java%20CI/badge.svg)
![GitHub last commit (branch)](https://img.shields.io/github/last-commit/icgw/FCA-Map/master)
![Maven Central with version prefix filter](https://img.shields.io/maven-central/v/org.apache.maven/maven-repository-metadata/3.6.1)
![license](https://img.shields.io/github/license/icgw/FCA-Map)

> Ontology matching system based on formal concept analysis.

Formal Concept Analysis (FCA) is a well developed mathematical model for clustering individuals and structuring concepts.

## Table of Contents

- [Background](#background)
- [Install](#install)
- [Usage](#usage)
- [Contributing](#contributing)
- [References](#references)
- [License](#license)

## Background

Formal concept analysis is based on mathematical order theory. (See the [book](https://www.springer.com/gp/book/9783540627715) for more details)

The following is an example of a formal context (about the characters of [Marvel Cinematic Universe](https://marvelcinematicuniverse.fandom.com/wiki/Category:Characters)) and its derived concept lattice.

### Formal Context

|                        | ![Asgardian][asg] <br /> Asgardian | ![Avenger][ag] <br /> Avenger | ![Female][fml] <br /> Female | ![Human][hm] <br /> Human | ![Infinity Stones User][inf] <br /> Infinity Stones User | ![Male][ml] <br /> Male | ![Scientist][sci] <br /> Scientist | ![Villain][vln] <br /> Villain |
|:----------------------:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
| ![Black Widow][bw] <br /> Black Widow         |   | ✖ | ✖ | ✖ |   |   |   |   |
| ![Captain America][ca] <br /> Captain America |   | ✖ |   | ✖ |   | ✖ |   |   |
| ![Hela][hl] <br /> Hela                       | ✖ |   | ✖ |   |   |   |   | ✖ |
| ![Hulk][hk] <br /> Hulk                       |   | ✖ |   | ✖ | ✖ | ✖ | ✖ |   |
| ![Iron Man][im] <br /> Iron Man               |   | ✖ |   | ✖ | ✖ | ✖ | ✖ |   |
| ![Thanos][ts] <br /> Thanos                   |   |   |   |   | ✖ | ✖ |   | ✖ |
| ![Thor][tr] <br /> Thor                       | ✖ | ✖ |   |   |   | ✖ |   |   |

### Concept Lattice

This concept lattice is derived from the above context, only reserved simplified extent and intent as follows.

![complete-lattice](https://raw.githubusercontent.com/icgw/FCA-Map/master/.github/assets/example-concept-lattice-marvel.svg?sanitize=true)

## Install

This project depends upon:
- Maven 3.6.1
- Jena 3.12.0
- Java 1.8.0\_161

I recommend [IntelliJ IDEA](https://www.jetbrains.com/idea/)! :)

## Usage

```java
// Demo.java

import cn.amss.semanticweb.alignment.Mapping;
import cn.amss.semanticweb.model.OntModelWrapper;
import cn.amss.semanticweb.matching.LexicalMatcher;
import cn.amss.semanticweb.matching.MatcherFactory;

public class Demo {
  public static void main(String[] args) {
    String srcURL = "https://raw.githubusercontent.com/icgw/FCA-Map/master/src/test/resources/oaei/conference/Conference.owl";
    String tgtURL = "https://raw.githubusercontent.com/icgw/FCA-Map/master/src/test/resources/oaei/conference/ekaw.owl";

    OntModelWrapper source = new OntModelWrapper(srcURL);
    OntModelWrapper target = new OntModelWrapper(tgtURL);

    LexicalMatcher lm = MatcherFactory.createLexicalMatcher();

    lm.setSourceTargetOntModelWrapper(source, target);
    lm.setExtractType(true, true);

    Mapping mappings = new Mapping();
    lm.mapOntClasses(mappings);
    lm.mapDatatypeProperties(mappings);
    lm.mapObjectProperties(mappings);

    System.out.println(mappings);

    lm.close();
  }
}
```

## Contributing

See [the contributing file](.github/CONTRIBUTING.md).

PRs accepted.

### FCA-Map for matching biomedical ontologies

See [FCA-Map@liweizhuo001](https://github.com/liweizhuo001/FCA-Map) (**no longer maintained**), which system is primarily developed by _Mengyi Zhao_.

## Acknowledgements

This work has been supported by the National Key Research and Development Program of China under grant 2016YFB1000902, and the Natural Science Foundation of China grant 61621003.

## References

1. **Identifying mappings among knowledge graphs by formal concept analysis.** _Guowei Chen, Songmao Zhang._ OM@ISWC 2019: . \[[technical paper][1]\] \[[slides][7]\]

2. **FCAMap-KG results for OAEI 2019.** _Fei Chang, Guowei Chen, Songmao Zhang._ OM@ISWC 2019: . \[[OAEI paper][2]\]

2. **FCAMapX results for OAEI 2018.** _Guowei Chen, Songmao Zhang._ OM@ISWC 2018: 160-166. \[[OAEI paper][3]\]

3. **Matching biomedical ontologies based on formal concept analysis.** _Mengyi Zhao, Songmao Zhang, Weizhuo Li, Guowei Chen._ J. Biomedical Semantics **9**(1): 11:1-11:27 (2018). \[[publisher page][4]\]

4. **Identifying and validating ontology mappings by formal concept analysis.** _Mengyi Zhao, Songmao Zhang._ OM@ISWC 2016: 61-72. \[[technical paper][5]\]

5. **FCA-Map results for OAEI 2016.** _Mengyi Zhao, Songmao Zhang._ OM@ISWC 2016: 172-177. \[[OAEI paper][6]\]

## Maintainer

[@icgw](https://github.com/icgw)

## License

[GPLv3](LICENSE) © Guowei Chen

[im]: https://raw.githubusercontent.com/icgw/FCA-Map/master/.github/assets/iron-man.png
[tr]: https://raw.githubusercontent.com/icgw/FCA-Map/master/.github/assets/thor.png
[bw]: https://raw.githubusercontent.com/icgw/FCA-Map/master/.github/assets/black-widow.png
[hk]: https://raw.githubusercontent.com/icgw/FCA-Map/master/.github/assets/hulk.png
[ca]: https://raw.githubusercontent.com/icgw/FCA-Map/master/.github/assets/captain-america.png
[ts]: https://raw.githubusercontent.com/icgw/FCA-Map/master/.github/assets/thanos.png
[hl]: https://raw.githubusercontent.com/icgw/FCA-Map/master/.github/assets/hela.png
[hm]: https://raw.githubusercontent.com/icgw/FCA-Map/master/.github/assets/human.png
[ml]: https://raw.githubusercontent.com/icgw/FCA-Map/master/.github/assets/male.png
[fml]: https://raw.githubusercontent.com/icgw/FCA-Map/master/.github/assets/female.png
[sci]: https://raw.githubusercontent.com/icgw/FCA-Map/master/.github/assets/scientist.png
[ag]: https://raw.githubusercontent.com/icgw/FCA-Map/master/.github/assets/avenger.png
[vln]: https://raw.githubusercontent.com/icgw/FCA-Map/master/.github/assets/villain.png
[asg]: https://raw.githubusercontent.com/icgw/FCA-Map/master/.github/assets/asgardian.png
[inf]: https://raw.githubusercontent.com/icgw/FCA-Map/master/.github/assets/infinity.png
[1]: http://disi.unitn.it/~pavel/om2019/papers/om2019\_LTpaper3.pdf
[2]: http://www.dit.unitn.it/~pavel/om2019/papers/oaei19\_paper8.pdf
[3]: http://ceur-ws.org/Vol-2288/oaei18\_paper7.pdf
[4]: https://jbiomedsem.biomedcentral.com/articles/10.1186/s13326-018-0178-9
[5]: http://ceur-ws.org/Vol-1766/om2016\_Tpaper6.pdf
[6]: http://ceur-ws.org/Vol-1766/oaei16\_paper7.pdf
[7]: https://github.com/icgw/FCA-Map/releases/download/v1.0.0/om2019-slide-gc.pdf
