use euclid::{Box2D, Point2D, Size2D};
use palette::{
    blend::Equations,
    encoding::{Linear, Srgb},
    rgb::Rgb,
    Blend, Lab, LinSrgb, Mix,
};
use std::collections::HashMap;
use std::hash::{Hash, Hasher};

pub struct Px;

pub type PointPx = Point2D<u32, Px>;
pub type BoxPx = Box2D<u32, Px>;
pub type SizePx = Size2D<u32, Px>;

pub struct QuadMeta {
    color: Lab,
    distance: f32,
}

impl QuadMeta {
    pub fn new(color: Lab, distance: f32) -> Self {
        Self { color, distance }
    }

    pub fn default() -> Self {
        Self {
            color: Lab::default(),
            distance: 0.,
        }
    }
}

pub trait QuadMetaGetters {
    fn color(&self) -> Lab {
        self.color
    }

    fn distance(&self) -> f32 {
        self.distance
    }
}
impl QuadMetaGetters for QuadMeta {}

pub trait QuadMetaSetters {
    fn set_distance(&mut self, v: f32) {
        self.distance = v;
    }
}
impl QuadMetaSetters for QuadMeta {}

pub trait ImmutableQuadMeta: QuadMetaGetters {}
pub trait MutQuadMeta: QuadMetaGetters + QuadMetaSetters {}

pub struct Quad {
    bbox: BoxPx,
    is_unit: bool,
    nw: Option<Box<Quad>>,
    ne: Option<Box<Quad>>,
    sw: Option<Box<Quad>>,
    se: Option<Box<Quad>>,
    meta: Option<QuadMeta>,
}

impl Quad {
    fn new(bbox: BoxPx) -> Self {
        Quad {
            bbox,
            is_unit: bbox.size().lower_than(SizePx::new(1, 1)).all(),
            nw: None,
            ne: None,
            sw: None,
            se: None,
            meta: None,
        }
    }

    pub fn contains(&self, point: &PointPx) -> bool {
        return self.bbox.contains(*point);
    }

    pub fn insert(
        &mut self,
        meta_map: &mut HashMap<BoxPx, *const QuadMeta>,
        point: PointPx,
        color: Lab,
    ) {
        if !self.contains(&point) || self.is_unit_quad() {
            return;
        }

        if self.meta.is_none() {
            let new_meta = QuadMeta::new(color, 0.);
            self.meta.insert(new_meta);
            meta_map.insert(self.bbox, &new_meta)
        } else {
            let meta = self.meta.as_mut().unwrap();
            meta.color = meta.color.mix(&color, 0.5);
        }

        if (self.bbox.min.x + self.bbox.max.x) / 2 > point.x {
            if (self.bbox.min.y + self.bbox.max.y) / 2 > point.y {
                if self.nw.is_none() {
                    self.nw = Some(Box::new(Self::new(BoxPx {
                        min: Point2D::new(self.bbox.min.x, self.bbox.min.y),
                        max: Point2D::new(
                            (self.bbox.min.x + self.bbox.max.x) / 2,
                            (self.bbox.min.y + self.bbox.max.y) / 2,
                        ),
                    })));
                }
                self.nw.as_mut().unwrap().insert(meta_map, point, color);
            } else {
                if self.sw.is_none() {
                    self.sw = Some(Box::new(Self::new(BoxPx {
                        min: Point2D::new(self.bbox.min.x, (self.bbox.min.y + self.bbox.max.y) / 2),
                        max: Point2D::new((self.bbox.min.x + self.bbox.max.x) / 2, self.bbox.max.y),
                    })));
                }
                self.sw.as_mut().unwrap().insert(meta_map, point, color);
            }
        } else {
            if (self.bbox.min.y + self.bbox.max.y) / 2 > point.y {
                if self.ne.is_none() {
                    self.ne = Some(Box::new(Self::new(BoxPx {
                        min: Point2D::new((self.bbox.min.x + self.bbox.max.x) / 2, self.bbox.min.y),
                        max: Point2D::new(self.bbox.max.x, (self.bbox.min.y + self.bbox.max.y) / 2),
                    })));
                }
                self.ne.as_mut().unwrap().insert(meta_map, point, color);
            } else {
                if self.se.is_none() {
                    self.se = Some(Box::new(Self::new(BoxPx {
                        min: Point2D::new(
                            (self.bbox.min.x + self.bbox.max.x) / 2,
                            (self.bbox.min.y + self.bbox.max.y) / 2,
                        ),
                        max: Point2D::new(self.bbox.max.x, self.bbox.max.y),
                    })));
                }
                self.se.as_mut().unwrap().insert(meta_map, point, color);
            }
        }
    }

    pub fn is_unit_quad(&self) -> bool {
        return self.is_unit;
    }

    pub fn get_box(&self) -> BoxPx {
        return self.bbox;
    }
}

impl Hash for Quad {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.bbox.hash(state);
    }
}

pub struct QuadTree {
    root: Quad,
    meta_map: HashMap<BoxPx, *const QuadMeta>,
}

impl QuadTree {
    pub fn new(bbox: BoxPx) -> Self {
        Self {
            root: Quad::new(bbox),
            meta_map: HashMap::new(),
        }
    }

    pub fn insert(&mut self, point: PointPx, color: Lab) {
        self.root.insert(&mut self.meta_map, point, color);
    }

    pub fn get_meta_map(&self) -> &HashMap<BoxPx, *const QuadMeta> {
        &self.meta_map
    }

    pub fn get_mut_meta_map(&self) -> &HashMap<BoxPx, *const dyn MutQuadMeta> {
        &self.meta_map
    }
}
