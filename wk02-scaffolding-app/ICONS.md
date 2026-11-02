# Tab icons

The four tab icons are **Material Icons** — `home`, `email`, `list`, `star` —
downloaded from [google/material-design-icons][repo] and licensed
**Apache 2.0**. Free to use and modify, including in your submission, with no
attribution required.

They are black glyphs on a transparent background, supplied at all five
densities. Replace them with your own whenever you like — keep the filenames
and everything keeps working.

[repo]: https://github.com/google/material-design-icons

## Where to get more

**Material Symbols** — <https://fonts.google.com/icons> — Apache 2.0, free for
any use including coursework. Pick an icon → **Download** → PNG.

The whole set is also on GitHub at [google/material-design-icons][repo], laid
out as `png/<category>/<icon>/materialicons/<size>/<scale>/`. That is where
these four came from, which is why they line up with the density buckets
exactly.

Other packs that are safe to use, with attribution:
Icons8, Flaticon, Font Awesome (free tier). Check the licence before shipping
anything in a submission.

## What sizes you need

A tab icon is **24dp**. Android picks the file matching the screen density, so
supply all five:

| Folder | Pixels |
|---|---|
| `drawable-mdpi` | 24 × 24 |
| `drawable-hdpi` | 36 × 36 |
| `drawable-xhdpi` | 48 × 48 |
| `drawable-xxhdpi` | 72 × 72 |
| `drawable-xxxhdpi` | 96 × 96 |

Supply only one and it will look soft or wrong-sized on most phones.

**Faster route:** Android Studio → **Resource Manager** → **+** → *Import
Drawables*. Drop in one large PNG and it generates the density buckets for you.

## Naming

Lowercase, underscores, no digits at the start — `ic_home.png`, not
`Home Icon.png`. The filename becomes `R.drawable.ic_home`.

## Monochrome, on transparent

These are drawn with `Icon(...)`, which **tints** them with the theme colour —
that is what gives you the selected/unselected states for free.

- Use a **white or black shape on a transparent background**
- Any colour in the file is overwritten by the tint

If you want a full-colour image — a logo, a photo — use `Image(...)` instead
of `Icon(...)`, and it will keep its own colours.
