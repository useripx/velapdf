---
name: SwiftPDF
colors:
  surface: '#f7f9fb'
  surface-dim: '#d8dadc'
  surface-bright: '#f7f9fb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f4f6'
  surface-container: '#eceef0'
  surface-container-high: '#e6e8ea'
  surface-container-highest: '#e0e3e5'
  on-surface: '#191c1e'
  on-surface-variant: '#45464d'
  inverse-surface: '#2d3133'
  inverse-on-surface: '#eff1f3'
  outline: '#76777d'
  outline-variant: '#c6c6cd'
  surface-tint: '#565e74'
  primary: '#000000'
  on-primary: '#ffffff'
  primary-container: '#131b2e'
  on-primary-container: '#7c839b'
  inverse-primary: '#bec6e0'
  secondary: '#0058be'
  on-secondary: '#ffffff'
  secondary-container: '#2170e4'
  on-secondary-container: '#fefcff'
  tertiary: '#000000'
  on-tertiary: '#ffffff'
  tertiary-container: '#2a1700'
  on-tertiary-container: '#b87500'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dae2fd'
  primary-fixed-dim: '#bec6e0'
  on-primary-fixed: '#131b2e'
  on-primary-fixed-variant: '#3f465c'
  secondary-fixed: '#d8e2ff'
  secondary-fixed-dim: '#adc6ff'
  on-secondary-fixed: '#001a42'
  on-secondary-fixed-variant: '#004395'
  tertiary-fixed: '#ffddb8'
  tertiary-fixed-dim: '#ffb95f'
  on-tertiary-fixed: '#2a1700'
  on-tertiary-fixed-variant: '#653e00'
  background: '#f7f9fb'
  on-background: '#191c1e'
  surface-variant: '#e0e3e5'
typography:
  headline-xl:
    fontFamily: Inter
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.01em
  label-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 14px
    letterSpacing: 0.02em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 40px
  gutter: 20px
  margin-mobile: 16px
  margin-desktop: 64px
  container-max: 1200px
---
## Brand & Style

The design system is built on a foundation of **Modern Minimalism**, tailored for a high-utility document conversion utility. The brand personality is professional, efficient, and dependable—aiming to reduce the cognitive load of complex file management through extreme clarity and intentional whitespace.

The visual narrative avoids unnecessary ornamentation, focusing instead on structural integrity and precision. By utilizing a "Content-First" approach, the UI recedes to let the user's documents and actions take center stage. The emotional response should be one of "effortless control," where the interface feels both sophisticated and invisible.

## Colors

The palette is anchored by **Deep Navy** (`#0F172A`) for primary text and core structural elements, providing a sense of authority and professional stability. **Vibrant Blue** (`#3B82F6`) serves as the action color, used exclusively for primary calls-to-action and active states to guide the eye toward conversion goals.

The background uses **Crisp White** and a very light neutral gray (`#F8FAFC`) to define different functional zones without heavy borders. For the "Coming Soon" indicators, use a soft **Amber** or **Slate Gray** wash with high-contrast text to ensure legibility while signaling a non-interactive state.

## Typography

The typography system uses **Inter**, a typeface designed for screen legibility and technical precision. Headlines utilize tight letter-spacing and heavier weights to establish a clear hierarchy, while body text remains open and airy for maximum readability during long document tasks.

On mobile, the scale shifts to prioritize vertical space, reducing the headline sizes while maintaining generous touch targets for interactive labels. Use `label-sm` in all-caps only for "Coming Soon" badges or secondary metadata.

## Layout & Spacing

The layout follows a **Fixed Grid** model on desktop, centered within a 1200px container to prevent line lengths from becoming unreadable. A 12-column grid is used for the main dashboard, typically splitting the view into a 3-column sidebar and a 9-column workspace.

Spacing follows a strict 4px base unit. Generous "Negative Space" (40px+) is used between functional groups to ensure the UI feels calm. On mobile, the layout collapses to a single column with 16px side margins, utilizing horizontal scrolling for toolbars where necessary.

## Elevation & Depth

This design system uses **Ambient Shadows** to create a sense of layering without relying on heavy borders. Shadows are extremely subtle: high blur (15-30px), very low opacity (5-8%), and a slight vertical offset to simulate a light source from above.

Depth levels:

- **Level 0 (Floor):** The background (#F8FAFC).
- **Level 1 (Cards):** Main content containers with a 1px soft border (#E2E8F0) and no shadow.
- **Level 2 (Active Elements):** Elements being interacted with or floating (e.g., tooltips, dropdowns) receive a soft ambient shadow.
- **Level 3 (Modals):** Large floating surfaces with a deep, diffused shadow to pull focus.

## Shapes

The shape language is defined as **Rounded**, utilizing a 0.5rem (8px) corner radius for most standard UI elements like buttons, input fields, and small cards. This creates a friendly, modern approachable feel that balances the "Corporate" color palette.

Larger containers or primary "Drop Zones" for file uploads should use `rounded-xl` (24px) to emphasize them as significant areas of the interface. Badges and chips should use a fully rounded "Pill" shape.

## Components

### Buttons

- **Primary:** Vibrant Blue background, White text, 8px radius. Heavy padding (12px 24px).
- **Secondary:** Deep Navy outline (1px), Navy text, transparent background.
- **Tertiary:** No background, Blue text, for low-priority actions.

### Badges (Coming Soon)

- **Style:** Small pill shape, no border.
- **Amber Variant:** Background `#FFF7ED`, Text `#B45309`.
- **Gray Variant:** Background `#F1F5F9`, Text `#475569`.
- **Placement:** Top-right corner of component or inline next to text labels.

### Input Fields

- **Default:** White background, 1px `#E2E8F0` border, 8px radius.
- **Focus:** 2px Blue border with a soft blue outer glow (3px blur).

### Cards & File Items

- **File List Item:** White background, 1px soft bottom-border, 16px padding. Icons should be simplified, mono-line glyphs in Deep Navy.
- **Drop Zone:** Dashed border (2px), Blue tint background (5% opacity), centered icon and large headline.

### Progress Bars

- Slim 4px height, Vibrant Blue fill on a Slate-200 background, used during file conversion processes.
