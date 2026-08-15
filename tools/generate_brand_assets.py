#!/usr/bin/env python3
"""Generate the CCTV TV app icon and Android TV banner."""

from pathlib import Path

from PIL import Image, ImageDraw, ImageFont


ROOT = Path(__file__).resolve().parents[1]
SCALE = 4

COLORS = {
    "charcoal": "#10161B",
    "charcoal_light": "#182127",
    "screen": "#202C31",
    "ivory": "#F4F0E8",
    "amber": "#F2A33A",
    "amber_light": "#FFC56B",
    "teal": "#3AAFA9",
    "muted": "#AAB4B8",
}


def scaled(value: int) -> int:
    return value * SCALE


def rounded_rectangle(draw: ImageDraw.ImageDraw, box, radius, fill):
    draw.rounded_rectangle(tuple(scaled(v) for v in box), radius=scaled(radius), fill=fill)


def line(draw: ImageDraw.ImageDraw, points, fill, width):
    draw.line([(scaled(x), scaled(y)) for x, y in points], fill=fill,
              width=scaled(width), joint="curve")


def make_icon(size: int) -> Image.Image:
    base_size = 1024
    canvas = Image.new("RGB", (scaled(base_size), scaled(base_size)), COLORS["charcoal"])
    draw = ImageDraw.Draw(canvas)

    rounded_rectangle(draw, (28, 28, base_size - 28, base_size - 28), 210, COLORS["charcoal_light"])
    rounded_rectangle(draw, (52, 52, base_size - 52, base_size - 52), 184, COLORS["charcoal"])

    # Broadcast antenna. The wide spacing keeps the silhouette readable at launcher size.
    line(draw, ((405, 302), (314, 196)), COLORS["ivory"], 42)
    line(draw, ((619, 302), (710, 196)), COLORS["ivory"], 42)
    draw.ellipse((scaled(285), scaled(168), scaled(337), scaled(220)), fill=COLORS["teal"])
    draw.ellipse((scaled(687), scaled(168), scaled(739), scaled(220)), fill=COLORS["amber"])

    # Television body and screen.
    rounded_rectangle(draw, (174, 288, 850, 760), 116, COLORS["ivory"])
    rounded_rectangle(draw, (224, 338, 800, 664), 78, COLORS["screen"])
    rounded_rectangle(draw, (250, 364, 774, 638), 60, COLORS["charcoal_light"])

    # Play mark is intentionally oversized for recognition on Android TV home screens.
    draw.polygon(
        [(scaled(442), scaled(414)), (scaled(442), scaled(590)), (scaled(606), scaled(502))],
        fill=COLORS["amber"],
    )
    draw.polygon(
        [(scaled(458), scaled(435)), (scaled(458), scaled(569)), (scaled(582), scaled(502))],
        fill=COLORS["amber_light"],
    )

    # Channel indicators and feet balance the television silhouette.
    rounded_rectangle(draw, (344, 704, 484, 728), 12, COLORS["teal"])
    rounded_rectangle(draw, (500, 704, 680, 728), 12, COLORS["amber"])
    rounded_rectangle(draw, (292, 760, 430, 808), 24, COLORS["ivory"])
    rounded_rectangle(draw, (594, 760, 732, 808), 24, COLORS["ivory"])

    return canvas.resize((size, size), Image.Resampling.LANCZOS)


def load_font(size: int, bold: bool = False) -> ImageFont.FreeTypeFont:
    candidates = [
        "/System/Library/Fonts/STHeiti Medium.ttc" if bold
        else "/System/Library/Fonts/STHeiti Light.ttc",
        "/System/Library/Fonts/Supplemental/Arial Unicode.ttf",
        "/System/Library/Fonts/Supplemental/Arial Bold.ttf" if bold
        else "/System/Library/Fonts/Supplemental/Arial.ttf",
    ]
    for path in candidates:
        if Path(path).exists():
            return ImageFont.truetype(path, size=size)
    return ImageFont.load_default()


def make_banner() -> Image.Image:
    width, height = 320, 180
    canvas = Image.new("RGB", (scaled(width), scaled(height)), COLORS["charcoal"])
    draw = ImageDraw.Draw(canvas)
    rounded_rectangle(draw, (4, 4, width - 4, height - 4), 18, COLORS["charcoal_light"])

    icon = make_icon(128).resize((scaled(128), scaled(128)), Image.Resampling.LANCZOS)
    canvas.paste(icon, (scaled(16), scaled(26)))

    title_font = load_font(scaled(24), bold=True)
    subtitle_font = load_font(scaled(12))
    draw.text((scaled(154), scaled(58)), "CCTV 电视", font=title_font, fill=COLORS["ivory"])
    rounded_rectangle(draw, (154, 96, 194, 101), 2, COLORS["amber"])
    draw.text((scaled(154), scaled(112)), "直播 · 本地视频", font=subtitle_font, fill=COLORS["muted"])

    return canvas.resize((width, height), Image.Resampling.LANCZOS)


def main() -> None:
    readme_icon = make_icon(1024)
    readme_icon.save(ROOT / "docs/assets/cctv-tv-icon.png", optimize=True)
    readme_icon.resize((512, 512), Image.Resampling.LANCZOS).save(
        ROOT / "app/src/main/res/drawable/mytv_icon.png", optimize=True
    )
    make_banner().save(ROOT / "app/src/main/res/drawable-nodpi/tv_banner.png", optimize=True)


if __name__ == "__main__":
    main()
