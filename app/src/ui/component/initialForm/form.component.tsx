import { ProgressComponent, MessageBox, ButtonComponent } from "@/ui/index";
import writingSloth from "@/assets/image/initialForm/writing-sloth.svg";
import { useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowLeft } from "@fortawesome/free-solid-svg-icons";
import { ReactNode } from "react";

interface FormProps {
  progress: number;
  message: string;
  buttonTxt: string;
  children: ReactNode;
}

export const FormTemplate = (props: FormProps) => {
  const navigate = useNavigate();

  return (
    <section className="flex flex-col justify-between h-screen">
      <div className="container mt-16">
        <div className="flex gap-6 items-center">
          <FontAwesomeIcon
            className="text-neutral-200 text-3xl"
            icon={faArrowLeft}
          />
          <ProgressComponent progressValue={props.progress} />
        </div>

        <div className="mt-16 flex items-center gap-10">
          <img
            className="w-full max-w-36"
            src={writingSloth}
            alt="Preguiça com o pelo roxo, segurando uma caneta e um papel, olhando para o usuário enquanto escreve"
          />
          <MessageBox classname="font-medium text-lg w-full">
            {props.message}
          </MessageBox>
        </div>
        {props.children}
      </div>

      <div>
        <hr className="border-neutral-500" />
        <div className="flex justify-center items-center mt-11 mb-11">
          <ButtonComponent
            clickEvent={() =>
              navigate(`/bem-vindo/area`, { state: { step: "area" } })
            }
            btnType="button"
            classname="bg-primary-color hover:bg-primary-color-dark hover:border-primary-color border-secondary-color"
          >
            {props.buttonTxt}
          </ButtonComponent>
        </div>
      </div>
    </section>
  );
};
