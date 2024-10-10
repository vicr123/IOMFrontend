import Styles from "./mapItemModal.module.css";

export function ControlButton({text, onClick}: {
    text: string
    onClick: () => void
}) {
    return <div className={Styles.controlButton} onClick={onClick}>
        {text}
    </div>
}