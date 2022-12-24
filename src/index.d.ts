interface View {
  name?: string;
  size?: number;
  type: number;
  bold?: number;
  PaddingLeft: number;
  PaddingTop: number;
  PaddingRight: number;
  PaddingBottom: number;
  setViewVisibility?: boolean;
  color?: string;
  uri?: string;
  ZeroTime?: date | null;
  hide?: boolean;
}
interface CNViewItems extends Array<View> {}

interface CN {
  eventData: string;
  title: string;
  body: string;
  id: number;
  View: CNViewItems;
}
interface Type {
  Image: number;
  Text: number;
  Cronometer: number;
}
interface FB_TYPE {
  NORMAL: number;
  BOLD: number;
  ITALIC: number;
  BOLD_ITALIC: number;
}
interface CTN {
  payload: string;
  title: string;
  body: string;
  remove?: boolean;
  foreground: boolean;
  id: number;
  date?: date;
  isCountDown: boolean;
  setCustomContentView?: boolean;
}

export function onEvent(listener: function): Void;
export function CustomNotification(a: CN, cb: function): Void;
export function TimerNotification(a: CTN): Void;
export function RemoveTimer(n: number);
export const TYPES: Type;
export const FB_TYPE: FB_TYPE;
