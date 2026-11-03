"""Generate original pixel-art sprite sheets for the Arkanoid project."""
import math, random, sys
from PIL import Image, ImageDraw

OUT = sys.argv[1]
random.seed(233)

def save(img, name):
    img.save(f"{OUT}/{name}")

# ---------- paddle.png : 4 cols x 2 rows, 64x16 per frame ----------
# row 0 = normal paddle (lights run along the body while moving)
# row 1 = laser paddle (red cannons on both ends, glowing)
W, H = 64, 16
sheet = Image.new("RGBA", (W * 4, H * 2), (0, 0, 0, 0))
for row in range(2):
    for f in range(4):
        fr = Image.new("RGBA", (W, H), (0, 0, 0, 0))
        d = ImageDraw.Draw(fr)
        d.rounded_rectangle([0, 1, W - 1, H - 2], radius=6, fill=(150, 155, 175), outline=(60, 60, 80))
        d.rectangle([8, 3, W - 9, 6], fill=(210, 215, 230))
        cap = (220, 40, 40) if row == 0 else (255, 120, 30)
        d.rounded_rectangle([0, 1, 9, H - 2], radius=5, fill=cap, outline=(90, 10, 10))
        d.rounded_rectangle([W - 10, 1, W - 1, H - 2], radius=5, fill=cap, outline=(90, 10, 10))
        # running lights
        for i in range(4):
            lx = 14 + i * 10
            on = (i == f)
            d.rectangle([lx, 9, lx + 5, 11], fill=(80, 230, 255) if on else (70, 90, 110))
        if row == 1:
            glow = 150 + 25 * f
            d.rectangle([2, 0, 6, 4], fill=(255, glow, 60))
            d.rectangle([W - 7, 0, W - 3, 4], fill=(255, glow, 60))
        sheet.paste(fr, (f * W, row * H))
save(sheet, "paddle.png")

# ---------- ball.png : 4 cols x 3 rows, 16x16 ----------
# row 0 = rolling (highlight rotates), row 1 = bounce squash, row 2 = fire (piercing)
S = 16
sheet = Image.new("RGBA", (S * 4, S * 3), (0, 0, 0, 0))
for f in range(4):
    # rolling
    fr = Image.new("RGBA", (S, S), (0, 0, 0, 0)); d = ImageDraw.Draw(fr)
    d.ellipse([1, 1, 14, 14], fill=(235, 235, 245), outline=(120, 120, 140))
    a = f * math.pi / 2
    hx, hy = 7.5 + 3.5 * math.cos(a), 7.5 + 3.5 * math.sin(a)
    d.ellipse([hx - 2, hy - 2, hx + 2, hy + 2], fill=(255, 255, 255))
    sheet.paste(fr, (f * S, 0))
    # bounce squash: wide -> tall -> normal
    fr = Image.new("RGBA", (S, S), (0, 0, 0, 0)); d = ImageDraw.Draw(fr)
    sq = [(0, 4, 15, 11), (2, 2, 13, 13), (3, 0, 12, 15), (1, 1, 14, 14)][f]
    d.ellipse(sq, fill=(255, 255, 180), outline=(200, 160, 40))
    sheet.paste(fr, (f * S, S))
    # fire ball
    fr = Image.new("RGBA", (S, S), (0, 0, 0, 0)); d = ImageDraw.Draw(fr)
    r = 7 + (f % 2)
    d.ellipse([7.5 - r, 7.5 - r, 7.5 + r, 7.5 + r], fill=(255, 90 + 30 * f, 20, 200))
    d.ellipse([3, 3, 12, 12], fill=(255, 230, 120))
    sheet.paste(fr, (f * S, 2 * S))
save(sheet, "ball.png")

# ---------- bricks.png : 10 frames 50x20 ----------
BW, BH = 50, 20
def brick(base, cracks=0, pattern=None):
    fr = Image.new("RGBA", (BW, BH), (0, 0, 0, 0)); d = ImageDraw.Draw(fr)
    light = tuple(min(255, c + 70) for c in base); dark = tuple(max(0, c - 80) for c in base)
    d.rectangle([0, 0, BW - 1, BH - 1], fill=base)
    d.line([(0, 0), (BW - 1, 0)], fill=light, width=2); d.line([(0, 0), (0, BH - 1)], fill=light, width=2)
    d.line([(0, BH - 1), (BW - 1, BH - 1)], fill=dark, width=2); d.line([(BW - 1, 0), (BW - 1, BH - 1)], fill=dark, width=2)
    rnd = random.Random(cracks * 7 + base[0])
    for _ in range(cracks * 2):
        x = rnd.randint(6, BW - 6); y = 2
        pts = [(x, y)]
        while y < BH - 3:
            x += rnd.randint(-4, 4); y += rnd.randint(3, 6); pts.append((x, min(y, BH - 3)))
        d.line(pts, fill=(30, 30, 30), width=1)
    if pattern == "bomb":
        d.ellipse([18, 4, 31, 16], fill=(30, 30, 30)); d.line([(28, 5), (33, 1)], fill=(255, 220, 0), width=2)
    if pattern == "star":
        cx, cy = 25, 10
        pts = [(cx + (7 if i % 2 == 0 else 3) * math.cos(math.pi / 2 + i * math.pi / 5),
                cy - (7 if i % 2 == 0 else 3) * math.sin(math.pi / 2 + i * math.pi / 5)) for i in range(10)]
        d.polygon(pts, fill=(255, 255, 160))
    if pattern == "metal":
        for bx in (5, BW - 7):
            d.ellipse([bx, 7, bx + 4, 11], fill=(120, 90, 20))
    return fr

frames = [
    brick((60, 150, 240)),            # 0 normal
    brick((245, 160, 40)),            # 1 strong full
    brick((245, 160, 40), cracks=1),  # 2 strong cracked
    brick((150, 150, 165)),           # 3 hard full
    brick((150, 150, 165), cracks=1), # 4 hard cracked 1
    brick((150, 150, 165), cracks=2), # 5 hard cracked 2
    brick((220, 50, 50), pattern="bomb"),   # 6 explosive
    brick((60, 200, 90), pattern="star"),   # 7 bonus
    brick((215, 175, 50), pattern="metal"), # 8 unbreakable
    brick((255, 255, 255)),           # 9 hit flash
]
sheet = Image.new("RGBA", (BW * len(frames), BH), (0, 0, 0, 0))
for i, fr in enumerate(frames):
    sheet.paste(fr, (i * BW, 0))
save(sheet, "bricks.png")

# ---------- explosion.png : 6 frames 48x48 ----------
E = 48
sheet = Image.new("RGBA", (E * 6, E), (0, 0, 0, 0))
for f in range(6):
    fr = Image.new("RGBA", (E, E), (0, 0, 0, 0)); d = ImageDraw.Draw(fr)
    r = 6 + f * 3.5
    alpha = int(255 * (1 - f / 6))
    d.ellipse([24 - r, 24 - r, 24 + r, 24 + r], fill=(255, 140, 30, alpha))
    d.ellipse([24 - r * 0.6, 24 - r * 0.6, 24 + r * 0.6, 24 + r * 0.6], fill=(255, 240, 150, alpha))
    rnd = random.Random(f)
    for _ in range(8):
        a = rnd.random() * 2 * math.pi; dist = r + 2 + f
        px, py = 24 + dist * math.cos(a), 24 + dist * math.sin(a)
        d.rectangle([px - 1.5, py - 1.5, px + 1.5, py + 1.5], fill=(255, 220, 80, alpha))
    sheet.paste(fr, (f * E, 0))
save(sheet, "explosion.png")

# ---------- laser.png : 2 frames 6x16 ----------
sheet = Image.new("RGBA", (12, 16), (0, 0, 0, 0)); d = ImageDraw.Draw(sheet)
for f in range(2):
    ox = f * 6
    d.rectangle([ox + 1, 0, ox + 4, 15], fill=(255, 60, 60) if f == 0 else (255, 140, 140))
    d.rectangle([ox + 2, 0, ox + 3, 15], fill=(255, 255, 220))
save(sheet, "laser.png")

# ---------- powerup.png : 4 cols x 2 rows, 32x14 ----------
PW, PH = 32, 14
sheet = Image.new("RGBA", (PW * 4, PH * 2), (0, 0, 0, 0))
letters = {0: ((220, 50, 50), "L"), 1: ((50, 120, 230), "M")}
for row, (col, letter) in letters.items():
    for f in range(4):
        fr = Image.new("RGBA", (PW, PH), (0, 0, 0, 0)); d = ImageDraw.Draw(fr)
        d.rounded_rectangle([0, 0, PW - 1, PH - 1], radius=6, fill=col, outline=(20, 20, 20))
        shine = 2 + f * 7
        d.rectangle([shine, 2, shine + 3, PH - 3], fill=tuple(min(255, c + 90) for c in col))
        d.text((12, 1), letter, fill=(255, 255, 255))
        sheet.paste(fr, (f * PW, row * PH))
save(sheet, "powerup.png")

# ---------- backgrounds bg1..bg5 : 600x700 ----------
tints = [(16, 16, 42), (30, 12, 40), (10, 30, 35), (35, 22, 10), (40, 8, 18)]
for i, t in enumerate(tints, start=1):
    img = Image.new("RGB", (600, 700), t); d = ImageDraw.Draw(img)
    for y in range(700):
        k = y / 700
        c = tuple(int(v * (0.6 + 0.6 * k)) for v in t)
        d.line([(0, y), (600, y)], fill=c)
    # hex grid pattern
    for gy in range(0, 700, 40):
        for gx in range(0, 600, 46):
            ox = 23 if (gy // 40) % 2 else 0
            cx, cy = gx + ox, gy
            pts = [(cx + 14 * math.cos(math.pi / 3 * k), cy + 14 * math.sin(math.pi / 3 * k)) for k in range(6)]
            d.polygon(pts, outline=tuple(min(255, v + 25) for v in t))
    rnd = random.Random(i)
    for _ in range(80):
        x, y = rnd.randint(0, 599), rnd.randint(0, 699)
        b = rnd.randint(120, 255)
        d.point((x, y), fill=(b, b, b))
    img.save(f"{OUT}/bg{i}.png")
print("sprites generated")
