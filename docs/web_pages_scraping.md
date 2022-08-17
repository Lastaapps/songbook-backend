# Czech song chords scrapping

*(This document has been written using horrible level of English - well, I could do better, but I don't want to. I
didn't even try...)*

**Regex is pain.**

Regex expressions are usually divided into more parts to make (I hope) the matches more performant - end of a page is
skipped. The expression expect these additional regex options to be enabled: `case insensitive`, `dot matches newline`.
Also, first matches are simpler most of the time. You may need to escape some characters in other languages than Kotlin.

This page covers:

- http://agama2000.com
- https://brnkni.cz
- https://pisnicky-akordy.cz
- https://supermusic.sk
- https://velkyzpevnik.cz
- http://www.zpevnik.wz.cz

### Agama - agama2000.com

#### General search

http://www.agama2000.com/api/findByText?text=...

```json
[
  {
    "name": "Karel Kryl",
    "id": "h7vbprgtdgwjrrac5xbdfmopq",
    "songs": [
      {
        "id": "ra6yfrydhgtknmrntxacfghijlmo",
        "name": "Pochyby",
        "interpretId": [
          "h7vbprgtdgwjrrac5xbdfmopq"
        ]
      },
      ...
    ]
  },
  ...
]
```

#### Author's songs

http://www.agama2000.com/api/loadSongs?personId=$id

```json
{
  "loaded": true,
  "time": 1658061009396,
  "personId": "xrpdwqwsynqie4tqc12rkakafc",
  "songs": [
    {
      "id": "w4qmw3atau3iedcnu16zdhsjva",
      "lastId": "czehmn2dajmivbxgvhypdp7wwc",
      "name": "Name",
      "interpretId": [
        "xrpdwqwsynqie4tqc12rkakafc",
        "von2fyuwhondvkt374vuidodpa"
      ]
    },
    ...
  ]
}
```

#### Author name - id mapping

http://www.agama2000.com

```
"id":"([^"]+)"[^}]+"name":"([^"]+)","used":true
```

(id, name)

#### Song text

The web page of a song
http://www.agama2000.com/$interpretId/$interpretName/$songId

http://agama2000.com/api/loadDocument?docId=$songId

```json
{
  "name": "Sníh padá sníh",
  "xid": "4aqtyc673qgvjam3zxadeghijkop",
  "id": "rlodgxfxnxlwvlxkmxbghknop",
  "text": "E\nSníh padá sníh na saních\n       E            B\njede z dálí je deda mráz\nhttps://www.youtube.com/watch?v=K9INZr1Mir8",
  "type": "Song",
  "interprets": [
    "v7o9xeohne8cybodfxbdkln"
  ],
  "created": "2013-12-20T20:23:55.931+00:00",
  "isLast": true
}
```

##### YouTube (optional)

```
(https://www\.youtube\.com/watch\?v=.+)$
```

### Brnkni - brnkni.cz

https://www.brnkni.cz/hledat-pisen/?q=...&w={pisen|interpret}

#### Song list

```
<ul[^<>]*class="songs"[^<>]*>((?>(?!</ul>).)*)</ul>

<li[^<>]*>[^<>]*<div[^<>]*class="about"[^<>]*>[^<>]*<h3><a[^<>]*href="([^"]*)"[^<>]*>([^<>]*)</a>[^<>]*</h3>[^<>]*<p[^<>]*class="author"[^<>]*>[^<>]*<a[^<>]*href="[^"]*"[^<>]*>([^<>]*)</a>[^<>]*</p>[^<>]*<p[^<>]*class="text"[^<>]*>([^<>]*)</p>[^<>]*</div>[^<>]*<div[^<>]*>[^<>]*<p>([^<>]*)</p>
```

(songLink, songName, authorName, type)
types: `Akordy`, `Taby`, `Akordy + taby`

##### No songs found

```
Bohužel, na hledaný výraz nebyla nalezena žádná píseň
```

#### Author list

```
<ul[^<>]*class="list"[^<>]*>((?>(?!</ul>).)*)</ul>

<li[^<>]*>[^<>]*<h2>[^<>]*<a[^<>]*href="([^"]*)"[^<>]*>([^<>]*)</a>[^<>]*</h2>[^<>]*<p[^<>]*>(\d+)[^<>]*</p>
```

(authorLink, authorName, songNumber)

##### No authors found

```
Bohužel, na hledaný výraz nebyl nalezen žádný interpret
```

#### Songs by an author

```
<ul[^<>]*class="songs"[^<>]*>((?>(?!</ul>).)*)</ul>

<li[^<>]*>[^<>]*<div[^<>]*class="about"[^<>]*>[^<>]*<h3><a[^<>]*href="([^"]*)"[^<>]*>([^<>]*)</a>[^<>]*</h3>[^<>]*<p[^<>]*class="text"[^<>]*>[^<>]*</p>[^<>]*</div>[^<>]*<div[^<>]*>[^<>]*<p>([^<>]*)</p>
```

(link, name, type)
types: `Akordy`, `Taby`, `Akordy + taby`

#### Song content

```
<div[^<>]*class="[^"]*text[^"]*"[^<>]*>((?>(?!</div>).)*)</div>
```

replace `<span[^<>]*>` → `[`,  `</span>` → `]`
replace `^<p[^<>]*>` → ` `,  `</p>$` → ` ` (tabs)

##### Song and author name

```
<div class="title">[^<>]*<h1>([^<>]+)<span>([^<>]*)</span>[^<>]*</h1>[^<>]*<h2><a[^<>]*>([^<>]*)</a>
```

(name, type, author)

### Písničky s akordy - pisnicky-akordy.cz

#### Song list

Pretty slow, ~ 2 s

https://pisnicky-akordy.cz/index.php?option=com_lyrics&task=ajax.display&format=json&tmpl=component&q=...&interpreters=true&songs=true&albums=true&limit=100

```json
[
  {
    "id": "42420",
    "name": "Name",
    "image": "images/.../img.png",
    "typ": "{interpret|album|p\u00edsni\u010dka}",
    "interpret": "name",
    "link": "\/link"
  },
  ...
]
```

#### Parsing author song list

```
<div[^<>]*id="az"[^<>]*>(?>(?!<ul).)*<ul[^<>]*>((?>(?!</ul>).)*)</ul>((?>(?!</div>).)*)</div>

<li[^<>]*>(?>(?!<a).)*<a[^<>]*href="((?>(?!").)+)"[^<>]*>(?>(?!<i).)*<i[^<>]*class='((?>(?!').)+)'[^<>]*>(?>(?!<span).)*<span[^<>]*>((?>(?!</span).)*)</span>
```

(link, type, name)
type (contains, check order is important): `glyphicon-music` → `chords`, `glyphicon-` → `work in progress song`, else
→ `text`

#### Parsing song text

```
<div[^<>]* id="songtext"[^<>]*>[^<>]*<pre[^<>]*>((?>(?!</pre).)*)</pre>[^<>]*</div>
```

remove `<el[^<>]*>`, `</el>`
replace `<span[^<>]*>` →`[`,  `</span>` → `]`

##### Song and author name

```
<div[^<>]*id="songheader"[^<>]*>[^<>]*<h1>[^<>]*<a[^<>]*>([^<>]*)</a>[^<>]*</h1>(?>(?!<a).)*<a[^<>]*>([^<>]*)</a>
```

### Supermusic - supermusic.sk

https://supermusic.cz/najdi.php?fraza=on&hladane=...&typhladania={piesen|skupina|textpisen}

#### Interprets parsing

```
Prebehlo vyhľadávanie slov:.*<table>((?>(?!</table>).)*)</table>

<tr>((?>(?!</tr>).)*)</tr>

<tr><td[^<>]*>((?>(?!</tr>).)+)</td></tr>

</td>(?>(?!<a).)*<a href="skupina\.php\?idskupiny=(\d+)"[^/]*>([^<]*)<(?>(?!</a>).)*</a>(?>(?!\().)*\(piesní: (\d+)\)
```

(id, name, songs)

##### No interprets found

```
Počet nájdených interpretov s '.*' v názve: (?>(?!<[^<>]*br[^<>]*>).)*0(?>(?!<[^<>]*br[^<>]*>).)*<[^<>]*br[^<>]*>
```

#### Author song list

https://supermusic.cz/skupina.php?idskupiny=123

```
Pridať novú pesničku((?>(?!<\/table>).)*)<\/table>

</td>(?>(?!<\/td>).)*<td>(?>(?!<a).)*<a href="skupina\.php\?idskupiny=(\d+)"[^\/]*>([^<]*)(?>(?!\().)*\(piesní: (\d+)\)
```

(type, id, name)

types: `akordy` → `CHORDS`, `texty` → `TEXT`, `taby` → `TAB`, `melodie` → `NOTES`, `preklady` → `TRANSLATION`

##### Interpreter has no songs

```
Neboli nájdené žiadne piesne
```

#### Search by song name or song text

```
<table[^<>]>.*Prebehlo vyhľadávanie slov:((?>(?!</table>).)*)</table>

<a[^<>]* href="[^"]*idpiesne=(\d+)[^"]*"[^<>]*><b>([^<>]*)</b></a> - ([^<>]) \(<a[^<>]*>([^<>]*)</a>\)
```

(id, name, type, author)

types: `akordy` → `CHORDS`, `texty` → `TEXT`, `taby` → `TAB`, `melodie` → `NOTES`, `preklady` → `TRANSLATION`

#### Song text

https://supermusic.cz/skupina.php?idpiesne=123456

##### Song has chords

```
<font color=black><script LANGUAGE="JavaScript">(?>(?!</script>).)*</script>((?>(?!<script).)*)<script
```

##### Song is a tab

```
<font color=black><pre><pre>((?>(?!</pre).)*)</pre></pre></font>
```

##### Song is melody and plain text

```
<font color=black>((?>(?!</font).)*)</font>
```

remove `<sup>`, `</sup>`, `<pre>`, `</pre>`, `<div[^<>]*>`, `</div>`, `<img[^<>]*>`

replace `<a[^<>]*>` → `[`, `<\/a>` → `]`, `<[^b>]*br[^<>]*>` → `'\n'`

##### Song and author name

```
<font class="test3">([^-]*)-([^<]*)</font>
```

(author, name)

##### YouTube (optional)

```
(https://www\.youtube\.com/embed/\w+)
```

remove `embed/`

### Velký zpěvník - velkyzpevnik.cz

https://www.velkyzpevnik.cz/vyhledavani/...

##### General

try to remove `^- ` in author names, sometimes this may appear

#### Selects song matched by name

```
<section[^<>]*id="pisne"[^<>]*>((?>(?!</section>).)*)</section>

class="title"[^<>]*href="([^"]*)">([^<]*)</a>[^<]*<a[^<>]*class="interpret" href="([^"]*)">([^<]*)</a>
```

(songLink, songName, authorLink, authorName)

#### Selects songs matched by text

```
<section[^<>]* id="pisne-dle-textu"[^<>]*>((?>(?!</section>).)*)</section>

class="title" href="([^"]*)">([^<]*)</a>[^<]*<a[^<>]*class="interpret" href="([^"]*)">([^<]*)</a>
```

(songLink, songName, authorLink, authorName)

#### Select matched authors

```
<section[^<>]* id="interpreti"[^<>]*>((?>(?!</section>).)*)</section>

<a[^<>]*href="([^"]*)"[^<>]*>(?>(?!<p).)*<p class="title">([^<]*)</p>
```

(link, name)

#### Author songs

```
<div[^<>]*class="songs"[^<>]*>((?>(?!</div>).)*)</div>

<a[^<>]*href="([^"]+)"[*<>]*>[^<>]*<p[^<>]*class="song-title"[^<>]*>([^<>]*)</p>
```

(link, name)

#### Song test

```
<div[^<>]*>[^<>]*<pre[^<>]*>(.*)</pre>[^<>]*</div>
```

replace `<span[^<>]*>` → `[`,  `</span>` → `]`

##### Song and author name

```
<article[^<>]*class="song"[^<>]*>[^<>]*<h1[^<>]*>([^<>]*)</h1>[^<>]*<h3>[^<>]*<a[^<>]*title="Detail interpreta"[^<>]*>([^<>]*)</a>
```

(name, author)

### Zpěvník s akordy - zpevnik.wz.cz

#### Search song list

http://www.zpevnik.wz.cz/index.php?nazev=...&slova=...&autor=...&pg=vysledky_vyhledavani

```
<div[^<>]* class="songy"[^<>]*>(.*)</div>

<li[^<>]*><a[^<>]* href="\?id=(\d+)"[^<>]*>([^<>]+)</a>[^<>]*\(<a[^<>]* href="\?id=(\d+)"[^<>]*>([^<]*)</a>\)</li>
```

(songId, songName, authorId, authorName)
Search by text is really slow, ~ 8 s.

#### Song text

http://zpevnik.wz.cz/index.php?id=...

```
<div[^<>]*class="song"[^<>]*>((?>(?!<\/div>).)*)<\/div>
```

##### Song and author name

```
<h1>([^()]*)\(<a[^<>]*>([^<>]*)</a>[^<>]*\)[^<>]*</h1>[^<>]*<div[^<>]*class="song"[^<>]*>
```

(name, author)

##### YouTube link

```
www\.youtube\.com/embed/([a-zA-Z0-9-_]+)
```

(videoId)
